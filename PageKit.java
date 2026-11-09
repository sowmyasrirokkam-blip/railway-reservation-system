import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PageKit {

    public static final Color BLUE = Color.web("#2563eb");
    public static final Color TEXT = Color.web("#111827");
    public static final Color GREY = Color.web("#6b7280");
    public static final Color BG = Color.web("#f3f5f9");
    public static final Color GREEN = Color.web("#2e8b57");
    public static final Color RED = Color.web("#c0392b");
    public static final Color ORANGE = Color.web("#f97316");
    public static Region spacer2() {
        Region r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }
    public static final String[] TABS = {"Home", "Trains", "Book Ticket", "Cancel Ticket", "History"};

    // ---------- top navigation bar (same on every page) ----------
    public static HBox nav(String active, Consumer<String> go) {
        Label logo = txt("RailWay", 24, FontWeight.BOLD, BLUE);
        HBox tabs = new HBox(8);
        for (String name : TABS) {
            Button b = new Button(name);
            boolean on = name.equals(active);
            b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            b.setCursor(Cursor.HAND);
            b.setTextFill(on ? BLUE : GREY);
            b.setBackground(Background.EMPTY);
            b.setBorder(on ? new Border(new BorderStroke(BLUE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 3, 0))) : Border.EMPTY);
            b.setOnMouseEntered(e -> { if (!on) b.setTextFill(TEXT); });
            b.setOnMouseExited(e -> { if (!on) b.setTextFill(GREY); });
            b.setOnAction(e -> go.accept(name));
            tabs.getChildren().add(b);
        }
        Label tag = txt("Ticket Counter", 13, FontWeight.NORMAL, GREY);
        HBox bar = new HBox(30, logo, tabs, spacer(), tag);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 24, 4, 24));
        bar.setBackground(Theme.solid(Color.WHITE, 0));
        bar.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.12)));
        return bar;
    }

    // page = top bar + content on a light background
    public static BorderPane page(String active, Consumer<String> go, Node content) {
        BorderPane p = new BorderPane();
        p.setBackground(Theme.solid(BG, 0));
        p.setTop(nav(active, go));
        BorderPane.setMargin(content, new Insets(18));
        p.setCenter(content);
        return p;
    }

    // ---------- small helpers ----------
    public static Label txt(String s, double size, FontWeight w, Color c) {
        Label l = new Label(s);
        l.setFont(Font.font("Segoe UI", w, size));
        l.setTextFill(c);
        return l;
    }

    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static void card(Region r) {
        r.setBackground(Theme.solid(Color.WHITE, 12));
        r.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.08)));
    }

    public static Label chip(String s, Color c) {
        Label l = txt(s, 12, FontWeight.BOLD, Color.WHITE);
        l.setBackground(Theme.solid(c, 12));
        l.setPadding(new Insets(3, 12, 3, 12));
        return l;
    }

    public static Button button(String text, Color c) {
        Button b = new Button(text);
        Theme.button(b, c, c.darker());
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        return b;
    }

    public static void field(TextField f, String prompt, double width) {
        f.setPromptText(prompt);
        f.setPrefWidth(width);
        f.setBackground(Theme.solid(Color.web("#f3f4f6"), 8));
        f.setBorder(new Border(new BorderStroke(Color.web("#d1d5db"), BorderStrokeStyle.SOLID,
                new CornerRadii(8), BorderWidths.DEFAULT)));
    }

    public static VBox labeled(String label, Node n) {
        return new VBox(4, txt(label, 12, FontWeight.NORMAL, GREY), n);
    }

    public static ScrollPane scroll(VBox content) {
        content.setBackground(Theme.solid(BG, 0));
        content.setPadding(new Insets(4));
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setBackground(Background.EMPTY);
        sp.setBorder(Border.EMPTY);
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    public static void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public static HBox row(String label, String value) {
        HBox h = new HBox(txt(label, 13, FontWeight.NORMAL, GREY), spacer(),
                txt(value, 13, FontWeight.BOLD, TEXT));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    // ---------- ticket card (used by Book and Cancel pages) ----------
    public static VBox ticketView(ReservationService s, Ticket t) {
        Train tr = null;
        for (Train x : s.getAllTrains()) {
            if (x.getTrainNo().equals(t.getTrainNo())) tr = x;
        }
        boolean booked = t.getStatus().equals("BOOKED");

        VBox titles = new VBox(2, txt("E-TICKET", 12, FontWeight.BOLD, Color.web("#bfdbfe")),
                txt(t.getTicketNo(), 24, FontWeight.BOLD, Color.WHITE));
        HBox top = new HBox(titles, spacer(), chip(t.getStatus(), booked ? GREEN : RED));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(16));
        top.setBackground(new Background(new BackgroundFill(BLUE,
                new CornerRadii(12, 12, 0, 0, false), Insets.EMPTY)));

        String from = tr == null ? "-" : tr.getSource();
        String to = tr == null ? "-" : tr.getDestination();
        HBox route = new HBox(12, txt(from, 22, FontWeight.BOLD, TEXT),
                txt("->", 18, FontWeight.NORMAL, ORANGE), txt(to, 22, FontWeight.BOLD, TEXT));
        route.setAlignment(Pos.CENTER_LEFT);

        Region dash = new Region();
        dash.setMinHeight(1);
        dash.setBorder(new Border(new BorderStroke(Color.web("#d1d5db"), BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0))));

        VBox body = new VBox(10, route, dash,
                row("Train", tr == null ? t.getTrainNo() : tr.getTrainNo() + " - " + tr.getName()),
                row("Departure", tr == null ? "-" : tr.getDepartureTime()),
                row("Passenger", t.getPassenger().getName() + " (" + t.getPassenger().getAge()
                        + ", " + t.getPassenger().getGender() + ")"),
                row("Seat No", String.valueOf(t.getSeatNo())),
                row("Class", t.getClassType()),
                row("Fare", "Rs. " + (int) t.getFare()));
        body.setPadding(new Insets(16));

        VBox card = new VBox(top, body);
        card(card);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        return card;
    }
}