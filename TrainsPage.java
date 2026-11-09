import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TrainsPage implements Page{

    private static final Color BLUE = Color.web("#2563eb");
    private static final Color TEXT = Color.web("#111827");
    private static final Color GREY = Color.web("#6b7280");
    private static final Color ORANGE = Color.web("#f97316");
    private static final Color GREEN = Color.web("#65d34a");

    private final ReservationService service;
    private final Consumer<String> go;
    private final Consumer<Train> onBuy;

    private final BorderPane root = new BorderPane();
    private final VBox list = new VBox(14);
    private final VBox rightCol = new VBox(16);
    private final Label countLabel = new Label();

    private final TextField fromField = new TextField();
    private final TextField toField = new TextField();
    private final CheckBox onlyAvailable = new CheckBox("Only trains with seats");
    private final CheckBox morning = new CheckBox("00:00 - 12:00");
    private final CheckBox afternoon = new CheckBox("12:00 - 19:00");
    private final CheckBox evening = new CheckBox("19:00 - 24:00");
    public void search(String from, String to) {
        fromField.setText(from);
        toField.setText(to);
    }
    public TrainsPage(ReservationService service, Consumer<String> go, Consumer<Train> onBuy) {
        this.service = service;
        this.go = go;
        this.onBuy = onBuy;

        root.setBackground(Theme.solid(Color.web("#f3f5f9"), 0));
        root.setTop(new VBox(buildNav(), buildSearchBar()));

        HBox body = new HBox(16, buildFilter(), buildCenter(), rightCol);
        body.setPadding(new Insets(16));
        root.setCenter(body);

        fromField.textProperty().addListener((o, a, b) -> refresh());
        toField.textProperty().addListener((o, a, b) -> refresh());
        for (CheckBox c : new CheckBox[]{onlyAvailable, morning, afternoon, evening}) {
            c.setOnAction(e -> refresh());
        }
        refresh();
    }

    public Region getRoot() { return root; }

    // ---------------- top bar ----------------
   private HBox buildNav() {
        return PageKit.nav("Trains", go);
    }

    private HBox buildSearchBar() {
        styleField(fromField, "e.g. Bangalore");
        styleField(toField, "e.g. Delhi");
        Button search = blueButton("Search Ticket");
        search.setPrefHeight(38);
        search.setOnAction(e -> refresh());

        HBox bar = new HBox(20,
                new VBox(4, text("From", 12, FontWeight.NORMAL, GREY), fromField),
                new VBox(4, text("To", 12, FontWeight.NORMAL, GREY), toField),
                spacer(), search);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setBackground(Theme.solid(Color.WHITE, 0));
        return bar;
    }

    // ---------------- left filter ----------------
    private VBox buildFilter() {
        Button reset = new Button("Reset");
        reset.setBackground(Background.EMPTY);
        reset.setTextFill(BLUE);
        reset.setCursor(javafx.scene.Cursor.HAND);
        reset.setOnAction(e -> {
            fromField.clear();
            toField.clear();
            onlyAvailable.setSelected(false);
            morning.setSelected(false);
            afternoon.setSelected(false);
            evening.setSelected(false);
            refresh();
        });

        HBox head = new HBox(text("Filter", 20, FontWeight.BOLD, TEXT), spacer(), reset);
        head.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(12, head,
                text("Availability", 15, FontWeight.BOLD, TEXT), onlyAvailable,
                text("Departure", 15, FontWeight.BOLD, TEXT), morning, afternoon, evening);
        box.setPadding(new Insets(18));
        box.setPrefWidth(200);
        box.setMinWidth(200);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        card(box, 12);
        return box;
    }

    // ---------------- centre list ----------------
    private VBox buildCenter() {
        list.setPadding(new Insets(4));
        list.setBackground(Theme.solid(Color.web("#f3f5f9"), 0));

        ScrollPane sp = new ScrollPane(list);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setBackground(Background.EMPTY);
        sp.setBorder(Border.EMPTY);
        VBox.setVgrow(sp, Priority.ALWAYS);

        HBox head = new HBox(10, text("Select Train", 22, FontWeight.BOLD, TEXT), countLabel);
        head.setAlignment(Pos.BASELINE_LEFT);
        countLabel.setFont(Font.font("Segoe UI", 12));
        countLabel.setTextFill(GREY);

        VBox center = new VBox(12, head, sp);
        HBox.setHgrow(center, Priority.ALWAYS);
        return center;
    }

    // ---------------- filtering ----------------
    private boolean matches(Train t) {
        String f = fromField.getText().trim().toLowerCase();
        String to = toField.getText().trim().toLowerCase();
        if (!t.getSource().toLowerCase().contains(f)) return false;
        if (!t.getDestination().toLowerCase().contains(to)) return false;
        if (onlyAvailable.isSelected() && t.getAvailableSeats() == 0) return false;

        boolean anyTime = morning.isSelected() || afternoon.isSelected() || evening.isSelected();
        if (anyTime) {
            int h = 0;
            try { h = Integer.parseInt(t.getDepartureTime().split(":")[0].trim()); }
            catch (Exception e) { }
            boolean ok = (morning.isSelected() && h < 12)
                    || (afternoon.isSelected() && h >= 12 && h < 19)
                    || (evening.isSelected() && h >= 19);
            if (!ok) return false;
        }
        return true;
    }

    // ---------------- refresh everything ----------------
    public void refresh() {
        list.getChildren().clear();
        int shown = 0;
        for (Train t : service.getAllTrains()) {
            if (matches(t)) {
                list.getChildren().add(trainCard(t));
                shown++;
            }
        }
        if (shown == 0) {
            list.getChildren().add(text("No trains found", 18, FontWeight.BOLD, GREY));
        }
        countLabel.setText("(" + shown + " trains found)");
        rightCol.getChildren().setAll(buildDonutCard(), buildSummaryCard());
    }

    // ---------------- one train card ----------------
    private VBox trainCard(Train t) {
        int a = t.getAvailableSeats();
        Color seatColor = a == 0 ? Color.web("#c0392b") : a <= 2 ? Color.web("#e08e0b") : Color.web("#2e8b57");
        String seatText = a == 0 ? "Full" : a + " seats left";

        // row 1: route and seats badge
        VBox names = new VBox(2,
                text(t.getSource() + "  ->  " + t.getDestination(), 14, FontWeight.BOLD, TEXT),
                text(t.getTrainNo() + " - " + t.getName(), 11, FontWeight.NORMAL, GREY));
        Label seats = text(seatText, 12, FontWeight.BOLD, Color.WHITE);
        seats.setBackground(Theme.solid(seatColor, 12));
        seats.setPadding(new Insets(3, 12, 3, 12));
        HBox row1 = new HBox(names, spacer(), seats);
        row1.setAlignment(Pos.CENTER_LEFT);

        // row 2: times and route line
        VBox dep = timeBlock(t.getDepartureTime(), code(t.getSource()) + " - Station", ORANGE, Pos.CENTER_LEFT);
        VBox arr = timeBlock(arrival(t), code(t.getDestination()) + " - Station", BLUE, Pos.CENTER_RIGHT);

        StackPane lineRow = new StackPane();
        Line line = new Line();
        line.setStroke(Color.web("#9ca3af"));
        line.getStrokeDashArray().addAll(5.0, 5.0);
        line.endXProperty().bind(lineRow.widthProperty().subtract(10));
        Rectangle icon = new Rectangle(18, 14);
        icon.setArcWidth(6);
        icon.setArcHeight(6);
        icon.setFill(Color.web("#374151"));
        StackPane iconBox = new StackPane(icon);
        iconBox.setBackground(Theme.solid(Color.WHITE, 0));
        iconBox.setPadding(new Insets(0, 6, 0, 6));
        iconBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        lineRow.getChildren().addAll(line, iconBox);

        int mins = durationMins(t);
        Label est = text("Estimate : " + (mins / 60) + "h " + (mins % 60) + "m", 11, FontWeight.NORMAL, GREY);
        VBox mid = new VBox(4, lineRow, est);
        mid.setAlignment(Pos.CENTER);
        HBox.setHgrow(mid, Priority.ALWAYS);

        HBox row2 = new HBox(14, dep, mid, arr);
        row2.setAlignment(Pos.CENTER);

        // row 3: facilities, price, buy
        HBox chips = new HBox(8, chip("GEN"), chip("SL"), chip("3A"), chip("2A"), chip("1A"));
        VBox fac = new VBox(6, text("Facilities", 12, FontWeight.BOLD, TEXT), chips);

        int fare = (int) Ticket.fareOf("General");
        HBox price = new HBox(4, text("Rs. " + fare, 18, FontWeight.BOLD, ORANGE),
                text("/ Person", 11, FontWeight.NORMAL, GREY));
        price.setAlignment(Pos.BASELINE_RIGHT);

        Button buy = blueButton(a == 0 ? "Sold Out" : "Buy Now");
        buy.setPrefWidth(120);
        if (a == 0) {
            buy.setDisable(true);
        } else {
            buy.setOnAction(e -> onBuy.accept(t));
        }
        VBox right = new VBox(6, price, buy);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox row3 = new HBox(fac, spacer(), right);
        row3.setAlignment(Pos.BOTTOM_LEFT);

        VBox card = new VBox(12, row1, row2, row3);
        card.setPadding(new Insets(16));
        card(card, 12);
        return card;
    }

    private VBox timeBlock(String time, String station, Color c, Pos align) {
        VBox v = new VBox(2, text(time, 26, FontWeight.BOLD, TEXT), text(station, 10, FontWeight.BOLD, c));
        v.setAlignment(align);
        v.setMinWidth(95);
        return v;
    }

    private Label chip(String s) {
        Label l = text(s, 11, FontWeight.NORMAL, Color.web("#374151"));
        l.setPadding(new Insets(3, 8, 3, 8));
        l.setBorder(new Border(new BorderStroke(Color.web("#d1d5db"), BorderStrokeStyle.SOLID,
                new CornerRadii(6), BorderWidths.DEFAULT)));
        return l;
    }

    // ---------------- right side: donut ----------------
    private VBox buildDonutCard() {
        int total = 0, free = 0;
        for (Train t : service.getAllTrains()) {
            total += t.getTotalSeats();
            free += t.getAvailableSeats();
        }
        int booked = total - free;
        double frac = total == 0 ? 0 : (double) booked / total;

        Circle ring = new Circle(70, 70, 56);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(GREEN);
        ring.setStrokeWidth(18);
        Pane p = new Pane(ring);
        p.setPrefSize(140, 140);
        p.setMinSize(140, 140);
        p.setMaxSize(140, 140);
        if (booked > 0) {
            Arc arc = new Arc(70, 70, 56, 56, 90, -Math.min(359.9, 360 * frac));
            arc.setType(ArcType.OPEN);
            arc.setFill(null);
            arc.setStroke(ORANGE);
            arc.setStrokeWidth(18);
            p.getChildren().add(arc);
        }

        VBox centreText = new VBox(0,
                text(Math.round(frac * 100) + "%", 26, FontWeight.BOLD, TEXT),
                text("Seats booked", 11, FontWeight.NORMAL, GREY));
        centreText.setAlignment(Pos.CENTER);
        centreText.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane donut = new StackPane(p, centreText);

        HBox legend = new HBox(14, legendItem(GREEN, "Available " + free), legendItem(ORANGE, "Booked " + booked));
        legend.setAlignment(Pos.CENTER);

        VBox box = new VBox(14, text("Seat availability", 16, FontWeight.BOLD, TEXT), donut, legend);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(18));
        box.setPrefWidth(230);
        box.setMinWidth(230);
        card(box, 12);
        return box;
    }

    private HBox legendItem(Color c, String s) {
        HBox h = new HBox(5, new Circle(5, c), text(s, 11, FontWeight.NORMAL, GREY));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private VBox buildSummaryCard() {
        int booked = 0, cancelled = 0;
        for (Ticket t : service.getBookingHistory()) {
            if (t.getStatus().equals("BOOKED")) booked++; else cancelled++;
        }
        VBox box = new VBox(10,
                text("Bookings", 16, FontWeight.BOLD, TEXT),
                legendRow("Active tickets", booked, Color.web("#2e8b57")),
                legendRow("Cancelled tickets", cancelled, Color.web("#c0392b")),
                legendRow("Trains running", service.getAllTrains().size(), BLUE));
        box.setPadding(new Insets(18));
        box.setPrefWidth(230);
        box.setMinWidth(230);
        card(box, 12);
        return box;
    }

    private HBox legendRow(String label, int value, Color c) {
        Label v = text(String.valueOf(value), 13, FontWeight.BOLD, Color.WHITE);
        v.setBackground(Theme.solid(c, 10));
        v.setPadding(new Insets(2, 10, 2, 10));
        HBox h = new HBox(text(label, 12, FontWeight.NORMAL, GREY), spacer(), v);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    // ---------------- helpers ----------------
    private int durationMins(Train t) {
        return 240 + ((t.getTrainNo().hashCode() & 0x7fffffff) % 12) * 30;
    }

    private String arrival(Train t) {
        try {
            String[] p = t.getDepartureTime().split(":");
            int total = Integer.parseInt(p[0].trim()) * 60 + Integer.parseInt(p[1].trim()) + durationMins(t);
            total %= 1440;
            return String.format("%02d:%02d", total / 60, total % 60);
        } catch (Exception e) {
            return "--:--";
        }
    }

    private String code(String city) {
        String c = city.trim().toUpperCase();
        return c.length() > 3 ? c.substring(0, 3) : c;
    }

    private Label text(String s, double size, FontWeight w, Color c) {
        Label l = new Label(s);
        l.setFont(Font.font("Segoe UI", w, size));
        l.setTextFill(c);
        return l;
    }

    private void card(Region r, double radius) {
        r.setBackground(Theme.solid(Color.WHITE, radius));
        r.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.08)));
    }

    private void styleField(TextField f, String prompt) {
        f.setPromptText(prompt);
        f.setPrefWidth(200);
        f.setBackground(Theme.solid(Color.web("#f3f4f6"), 8));
        f.setBorder(new Border(new BorderStroke(Color.web("#d1d5db"), BorderStrokeStyle.SOLID,
                new CornerRadii(8), BorderWidths.DEFAULT)));
    }

    private Button blueButton(String t) {
        Button b = new Button(t);
        Theme.button(b, BLUE, Color.web("#1d4ed8"));
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        return b;
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
}