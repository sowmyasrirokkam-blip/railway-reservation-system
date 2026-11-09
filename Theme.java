
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Theme {
    // coloured rounded label (used for train numbers, seat counts, status)
    public static Label badge(String text, Color bg) {
        Label l = new Label(text);
        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        l.setBackground(solid(bg, 20));
        l.setPadding(new Insets(6, 14, 6, 14));
        return l;
    }

    // see-through "glass" panel so the gradient shows behind the cards
    public static void glass(Region r) {
        r.setBackground(solid(Color.rgb(255, 255, 255, 0.12), 14));
    }

    // scroll area with a transparent background
    public static ScrollPane scroll(Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setBackground(Background.EMPTY);
        sp.skinProperty().addListener((o, a, b) -> {
            Node v = sp.lookup(".viewport");
            if (v instanceof Region) ((Region) v).setBackground(Background.EMPTY);
        });
        return sp;
    }

    // dark blue-teal gradient for the whole window
   public static Background gradientBackground() {
        return gradient("#07142b", "#16345f");
    }
    public static Background gradient(String c1, String c2) {
        LinearGradient g = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(c1)), new Stop(1, Color.web(c2)));
        return new Background(new BackgroundFill(g, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public static Background solid(Color c, double radius) {
        return new Background(new BackgroundFill(c, new CornerRadii(radius), Insets.EMPTY));
    }

    // big white title with a shadow
    public static void title(Label l, double size) {
        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, size));
        l.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.6)));
    }

    // rounded coloured button that changes colour and grows on hover
   public static void button(Button b, Color normal, Color hover) {
        b.setBackground(solid(normal, 8));
        b.setTextFill(Color.WHITE);
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        b.setCursor(Cursor.HAND);
        b.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.35)));
        b.setOnMouseEntered(e -> {
            b.setBackground(solid(hover, 8));
            b.setScaleX(1.03);
            b.setScaleY(1.03);
        });
        b.setOnMouseExited(e -> {
            b.setBackground(solid(normal, 8));
            b.setScaleX(1.0);
            b.setScaleY(1.0);
        });
    }

    // white rounded card for the content area
    public static void card(Region r) {
        r.setBackground(solid(Color.rgb(255, 255, 255, 0.94), 14));
    }
}