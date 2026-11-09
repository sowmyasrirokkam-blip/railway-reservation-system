import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HomePage implements Page {

    private final ReservationService service;
    private final StackPane root = new StackPane();

    private final TextField fromField = new TextField();
    private final TextField toField = new TextField();
    private final TextField pnrField = new TextField();
    private final ComboBox<String> vacTrain = new ComboBox<>();
    private final Label vacResult = new Label();

    private final Button[] tabBtns = new Button[3];
    private final Pane[] panels = new Pane[3];
    private final String[] tabNames = {"Book Ticket", "PNR Status", "Charts / Vacancy"};

    public HomePage(ReservationService service, Consumer<String> go, BiConsumer<String, String> onSearch) {
        this.service = service;
        root.setBackground(Theme.solid(Color.web("#0b1f1a"), 0));

        Pane scene = HeroScene.create();
        StackPane.setAlignment(scene, Pos.CENTER);

        // ---------- hero text ----------
        Label tag = white("SAFETY  |  SECURITY  |  PUNCTUALITY", 15, FontWeight.BOLD);
        Label title = white("Indian Railways", 76, FontWeight.BOLD);
        title.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.7)));
        Label sub = white("Heartily enjoy every journey through our boundless hospitality.\n"
                + "Through Indian Railways, the lifeline of the nation.", 17, FontWeight.NORMAL);
        VBox hero = new VBox(6, tag, title, sub);

        // ---------- search area ----------
        VBox search = buildSearch(onSearch);
        search.setMaxWidth(930);

        VBox centre = new VBox(hero, PageKit.spacer2(), search);
        centre.setPadding(new Insets(0, 70, 36, 70));
        VBox.setMargin(hero, new Insets(60, 0, 0, 0));

        BorderPane overlay = new BorderPane();
        overlay.setTop(buildNav(go));
        overlay.setCenter(centre);
        overlay.setMaxSize(HeroScene.W, HeroScene.H);

        root.getChildren().addAll(scene, overlay);
        StackPane.setAlignment(overlay, Pos.CENTER);
        select(0);
        refresh();
    }

    // ---------- top bar (transparent, white text) ----------
    private HBox buildNav(Consumer<String> go) {
        Label logo = white("RailWay", 26, FontWeight.BOLD);
        HBox tabs = new HBox(10);
        for (String name : PageKit.TABS) {
            boolean on = name.equals("Home");
            Button b = new Button(name);
            b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            b.setTextFill(on ? Color.WHITE : Color.web("#cbd5e1"));
            b.setBackground(Background.EMPTY);
            b.setCursor(Cursor.HAND);
            b.setBorder(on ? new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 3, 0))) : Border.EMPTY);
            b.setOnMouseEntered(e -> b.setTextFill(Color.WHITE));
            b.setOnMouseExited(e -> b.setTextFill(on ? Color.WHITE : Color.web("#cbd5e1")));
            b.setOnAction(e -> go.accept(name));
            tabs.getChildren().add(b);
        }
        Label counter = white("Ticket Counter", 13, FontWeight.NORMAL);
        counter.setPadding(new Insets(6, 14, 6, 14));
        counter.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
                new CornerRadii(4), BorderWidths.DEFAULT)));

        HBox nav = new HBox(40, logo, tabs, PageKit.spacer(), counter);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(22, 70, 10, 70));
        return nav;
    }

    // ---------- search card with 3 tabs ----------
    private VBox buildSearch(BiConsumer<String, String> onSearch) {
        HBox tabRow = new HBox(2);
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            Button b = new Button(tabNames[i]);
            b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            b.setCursor(Cursor.HAND);
            b.setPadding(new Insets(10, 22, 10, 22));
            b.setOnAction(e -> select(idx));
            tabBtns[i] = b;
            tabRow.getChildren().add(b);
        }

        // panel 1: book ticket
        PageKit.field(fromField, "From", 190);
        PageKit.field(toField, "To", 190);
        DatePicker date = new DatePicker(LocalDate.now());
        date.setPrefWidth(160);
        ComboBox<String> cls = new ComboBox<>();
        cls.getItems().add("All Class");
        cls.getItems().addAll(Ticket.CLASSES);
        cls.setValue("All Class");
        cls.setPrefWidth(130);
        Button go = PageKit.button("Search Train  >", PageKit.BLUE);
        go.setPrefHeight(38);
        go.setOnAction(e -> onSearch.accept(fromField.getText().trim(), toField.getText().trim()));
        HBox book = new HBox(14,
                PageKit.labeled("From", fromField), PageKit.labeled("To", toField),
                PageKit.labeled("Journey date", date), PageKit.labeled("Class", cls), bottom(go));
        book.setAlignment(Pos.BOTTOM_LEFT);

        // panel 2: PNR status
        PageKit.field(pnrField, "Ticket number, e.g. TKT1001", 300);
        Button check = PageKit.button("Check Status", PageKit.BLUE);
        check.setPrefHeight(38);
        check.setOnAction(e -> showPnr());
        HBox pnr = new HBox(14, PageKit.labeled("PNR / Ticket number", pnrField), bottom(check));
        pnr.setAlignment(Pos.BOTTOM_LEFT);

        // panel 3: vacancy
        vacTrain.setPromptText("Select a train");
        vacTrain.setPrefWidth(340);
        Button vac = PageKit.button("Check Vacancy", PageKit.BLUE);
        vac.setPrefHeight(38);
        vac.setOnAction(e -> showVacancy());
        vacResult.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        HBox vacRow = new HBox(14, PageKit.labeled("Train", vacTrain), bottom(vac), bottom(vacResult));
        vacRow.setAlignment(Pos.BOTTOM_LEFT);

        panels[0] = book;
        panels[1] = pnr;
        panels[2] = vacRow;

        StackPane card = new StackPane(book, pnr, vacRow);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(22));
        card.setBackground(Theme.solid(Color.WHITE, 12));
        card.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.5)));

        return new VBox(0, tabRow, card);
    }

    private VBox bottom(javafx.scene.Node n) {
        VBox v = new VBox(n);
        v.setAlignment(Pos.BOTTOM_LEFT);
        return v;
    }

    private void select(int idx) {
        for (int i = 0; i < 3; i++) {
            boolean on = i == idx;
            panels[i].setVisible(on);
            panels[i].setManaged(on);
            tabBtns[i].setTextFill(on ? Color.WHITE : Color.web("#e2e8f0"));
            tabBtns[i].setBackground(on ? Theme.solid(PageKit.BLUE, 6)
                    : Theme.solid(Color.rgb(15, 23, 42, 0.65), 6));
        }
    }

    // ---------- PNR status ----------
    private void showPnr() {
        String no = pnrField.getText().trim();
        Ticket found = null;
        for (Ticket t : service.getBookingHistory()) {
            if (t.getTicketNo().equalsIgnoreCase(no)) found = t;
        }
        if (found == null) {
            PageKit.error(no.isEmpty() ? "Please enter a ticket number" : "Ticket " + no + " not found");
            return;
        }
        Dialog<Void> d = new Dialog<>();
        d.setTitle("PNR Status");
        d.getDialogPane().setContent(PageKit.ticketView(service, found));
        d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        d.showAndWait();
    }

    // ---------- vacancy ----------
    private void showVacancy() {
        if (vacTrain.getValue() == null) {
            PageKit.error("Please select a train");
            return;
        }
        String no = vacTrain.getValue().split(" - ")[0];
        for (Train t : service.getAllTrains()) {
            if (t.getTrainNo().equals(no)) {
                int a = t.getAvailableSeats();
                vacResult.setText(a + " of " + t.getTotalSeats() + " seats available");
                vacResult.setTextFill(a == 0 ? PageKit.RED : PageKit.GREEN);
            }
        }
    }

    private Label white(String s, double size, FontWeight w) {
        Label l = PageKit.txt(s, size, w, Color.WHITE);
        l.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));
        return l;
    }

    public Region getRoot() { return root; }

    public void refresh() {
        String keep = vacTrain.getValue();
        vacTrain.setItems(FXCollections.observableArrayList());
        for (Train t : service.getAllTrains()) {
            vacTrain.getItems().add(t.getTrainNo() + " - " + t.getName());
        }
        vacTrain.setValue(keep);
        vacResult.setText("");
    }
}