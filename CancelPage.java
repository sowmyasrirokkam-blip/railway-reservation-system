import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class CancelPage implements Page {

    private final ReservationService service;
    private final BorderPane root;
    private final TextField numberField = new TextField();
    private final Button cancelBtn = PageKit.button("Cancel Ticket", PageKit.RED);
    private final Label message = new Label();
    private final VBox previewHolder = new VBox();
    private final VBox activeList = new VBox(8);

    public CancelPage(ReservationService service, Consumer<String> go) {
        this.service = service;

        PageKit.field(numberField, "e.g. TKT1001", 340);
        cancelBtn.setPrefSize(340, 42);
        cancelBtn.setDisable(true);
        cancelBtn.setOnAction(e -> cancel());
        numberField.textProperty().addListener((o, a, b) -> updatePreview());

        VBox form = new VBox(14,
                PageKit.txt("Cancel a ticket", 20, FontWeight.BOLD, PageKit.TEXT),
                PageKit.txt("Enter the ticket number to see the booking, then cancel it. "
                        + "The seat becomes available again.", 12, FontWeight.NORMAL, PageKit.GREY),
                PageKit.labeled("Ticket number", numberField),
                cancelBtn, message);
        ((Label) form.getChildren().get(1)).setWrapText(true);
        form.setPadding(new Insets(20));
        form.setPrefWidth(380);
        form.setMinWidth(380);
        form.setMaxHeight(Region.USE_PREF_SIZE);
        PageKit.card(form);

        previewHolder.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(previewHolder, Priority.ALWAYS);

        VBox right = new VBox(10, PageKit.txt("Active tickets", 16, FontWeight.BOLD, PageKit.TEXT),
                PageKit.scroll(activeList));
        right.setPadding(new Insets(16));
        right.setPrefWidth(290);
        right.setMinWidth(290);
        PageKit.card(right);

        root = PageKit.page("Cancel Ticket", go, new HBox(18, form, previewHolder, right));
        refresh();
    }

    private Ticket find(String no) {
        for (Ticket t : service.getBookingHistory()) {
            if (t.getTicketNo().equalsIgnoreCase(no.trim())) return t;
        }
        return null;
    }

    private void updatePreview() {
        String no = numberField.getText();
        if (no == null || no.trim().isEmpty()) {
            previewHolder.getChildren().clear();
            cancelBtn.setDisable(true);
            return;
        }
        Ticket t = find(no);
        if (t == null) {
            VBox box = new VBox(PageKit.txt("No ticket found", 16, FontWeight.BOLD, PageKit.RED));
            box.setAlignment(Pos.CENTER);
            box.setPrefHeight(120);
            PageKit.card(box);
            previewHolder.getChildren().setAll(box);
            cancelBtn.setDisable(true);
        } else {
            previewHolder.getChildren().setAll(PageKit.ticketView(service, t));
            cancelBtn.setDisable(!t.getStatus().equals("BOOKED"));
        }
    }

    private void cancel() {
        try {
            String no = numberField.getText().trim().toUpperCase();
            service.cancelTicket(no);
            message.setText("Ticket " + no + " cancelled. Seat is available again.");
            message.setTextFill(PageKit.GREEN);
            refresh();
            updatePreview();
        } catch (TicketNotFoundException ex) {
            message.setText(ex.getMessage());
            message.setTextFill(PageKit.RED);
            PageKit.error(ex.getMessage());
        }
    }

    public Region getRoot() { return root; }

    public void refresh() {
        activeList.getChildren().clear();
        List<Ticket> all = service.getBookingHistory();
        int n = 0;
        for (int i = all.size() - 1; i >= 0; i--) {
            Ticket t = all.get(i);
            if (!t.getStatus().equals("BOOKED")) continue;
            n++;
            VBox info = new VBox(2,
                    PageKit.txt(t.getTicketNo(), 14, FontWeight.BOLD, PageKit.TEXT),
                    PageKit.txt(t.getPassenger().getName(), 12, FontWeight.NORMAL, PageKit.GREY));
            Button pick = PageKit.button("Select", PageKit.BLUE);
            pick.setOnAction(e -> numberField.setText(t.getTicketNo()));
            HBox row = new HBox(info, PageKit.spacer(), pick);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setBackground(Theme.solid(Color.web("#f3f5f9"), 8));
            activeList.getChildren().add(row);
        }
        if (n == 0) {
            activeList.getChildren().add(PageKit.txt("No active tickets", 13, FontWeight.NORMAL, PageKit.GREY));
        }
        updatePreview();
    }
}