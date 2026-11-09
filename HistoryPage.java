import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class HistoryPage implements Page {

    private final ReservationService service;
    private final BorderPane root;
    private final HBox stats = new HBox(14);
    private final VBox list = new VBox(10);
    private final TextField search = new TextField();
    private final String[] names = {"All", "Booked", "Cancelled"};
    private final Button[] filterBtns = new Button[3];
    private String filter = "All";

    public HistoryPage(ReservationService service, Consumer<String> go) {
        this.service = service;

        PageKit.field(search, "Search ticket, name or train", 280);
        search.textProperty().addListener((o, a, b) -> refresh());

        HBox toolbar = new HBox(8, search, PageKit.spacer());
        toolbar.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 3; i++) {
            String name = names[i];
            Button b = new Button(name);
            b.setCursor(javafx.scene.Cursor.HAND);
            b.setOnAction(e -> { filter = name; refresh(); });
            filterBtns[i] = b;
            toolbar.getChildren().add(b);
        }

        VBox content = new VBox(14, stats, toolbar, PageKit.scroll(list));
        root = PageKit.page("History", go, content);
        refresh();
    }

    private VBox statCard(String title, String value, Color c) {
        VBox box = new VBox(2, PageKit.txt(value, 26, FontWeight.BOLD, c),
                PageKit.txt(title, 12, FontWeight.NORMAL, PageKit.GREY));
        box.setPadding(new Insets(14, 18, 14, 18));
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        PageKit.card(box);
        return box;
    }

    private HBox ticketRow(Ticket t) {
        boolean booked = t.getStatus().equals("BOOKED");
        Train tr = null;
        for (Train x : service.getAllTrains()) {
            if (x.getTrainNo().equals(t.getTrainNo())) tr = x;
        }
        Label no = PageKit.chip(t.getTicketNo(), booked ? PageKit.BLUE : Color.web("#9ca3af"));
        no.setMinWidth(100);
        no.setAlignment(Pos.CENTER);

        VBox who = new VBox(2,
                PageKit.txt(t.getPassenger().getName(), 15, FontWeight.BOLD, PageKit.TEXT),
                PageKit.txt(t.getPassenger().getAge() + " yrs, " + t.getPassenger().getGender(),
                        12, FontWeight.NORMAL, PageKit.GREY));
        who.setMinWidth(160);

        String route = tr == null ? t.getTrainNo() : tr.getSource() + " -> " + tr.getDestination();
        VBox trip = new VBox(2, PageKit.txt(route, 14, FontWeight.BOLD, PageKit.TEXT),
                PageKit.txt("Train " + t.getTrainNo() + "  |  Seat " + t.getSeatNo()
                        + "  |  " + t.getClassType(), 12, FontWeight.NORMAL, PageKit.GREY));
        HBox.setHgrow(trip, Priority.ALWAYS);

        Label fare = PageKit.txt("Rs. " + (int) t.getFare(), 16, FontWeight.BOLD, PageKit.ORANGE);
        Label status = PageKit.chip(t.getStatus(), booked ? PageKit.GREEN : PageKit.RED);
        status.setMinWidth(90);
        status.setAlignment(Pos.CENTER);

        HBox row = new HBox(16, no, who, trip, fare, status);
        row.setAlignment(Pos.CENTER_LEFT);
        if (booked) {
            Button c = new Button("Cancel");
            c.setCursor(javafx.scene.Cursor.HAND);
            c.setTextFill(PageKit.RED);
            c.setBackground(Theme.solid(Color.WHITE, 8));
            c.setBorder(new Border(new BorderStroke(PageKit.RED, BorderStrokeStyle.SOLID,
                    new CornerRadii(8), BorderWidths.DEFAULT)));
            c.setOnAction(e -> {
                try {
                    service.cancelTicket(t.getTicketNo());
                    refresh();
                } catch (TicketNotFoundException ex) {
                    PageKit.error(ex.getMessage());
                }
            });
            row.getChildren().add(c);
        }
        row.setPadding(new Insets(12, 16, 12, 16));
        PageKit.card(row);
        return row;
    }

    public Region getRoot() { return root; }

    public void refresh() {
        List<Ticket> all = service.getBookingHistory();
        int booked = 0, cancelled = 0, revenue = 0;
        for (Ticket t : all) {
            if (t.getStatus().equals("BOOKED")) { booked++; revenue += (int) t.getFare(); }
            else cancelled++;
        }
        stats.getChildren().setAll(
                statCard("Total bookings", String.valueOf(all.size()), PageKit.BLUE),
                statCard("Active tickets", String.valueOf(booked), PageKit.GREEN),
                statCard("Cancelled", String.valueOf(cancelled), PageKit.RED),
                statCard("Revenue (active)", "Rs. " + revenue, PageKit.ORANGE));

        for (int i = 0; i < 3; i++) {
            boolean on = names[i].equals(filter);
            Button b = filterBtns[i];
            b.setTextFill(on ? Color.WHITE : PageKit.GREY);
            b.setBackground(Theme.solid(on ? PageKit.BLUE : Color.WHITE, 8));
            b.setBorder(new Border(new BorderStroke(on ? PageKit.BLUE : Color.web("#d1d5db"),
                    BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
        }

        String key = search.getText() == null ? "" : search.getText().trim().toLowerCase();
        list.getChildren().clear();
        int shown = 0;
        for (int i = all.size() - 1; i >= 0; i--) {          // newest first
            Ticket t = all.get(i);
            boolean isBooked = t.getStatus().equals("BOOKED");
            if (filter.equals("Booked") && !isBooked) continue;
            if (filter.equals("Cancelled") && isBooked) continue;
            String hay = (t.getTicketNo() + " " + t.getPassenger().getName() + " "
                    + t.getTrainNo()).toLowerCase();
            if (!hay.contains(key)) continue;
            list.getChildren().add(ticketRow(t));
            shown++;
        }
        if (shown == 0) {
            list.getChildren().add(PageKit.txt("No bookings to show", 16, FontWeight.BOLD, PageKit.GREY));
        }
    }
}