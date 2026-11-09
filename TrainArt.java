import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.Random;

public class TrainArt {

    private static final double W = 800, H = 210;

    public static Pane create() {
        Pane pane = new Pane();
        pane.setPrefSize(W, H);
        pane.setMinSize(W, H);
        pane.setMaxSize(W, H);
        Rectangle clip = new Rectangle(W, H);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        pane.setClip(clip);

        // sky: deep navy at the top to warm orange at the horizon
        Rectangle sky = new Rectangle(W, H);
        sky.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0b1f3a")),
                new Stop(0.6, Color.web("#2a5d9f")),
                new Stop(1, Color.web("#f4a259"))));
        pane.getChildren().add(sky);

        // stars
        Random rnd = new Random(7);
        for (int i = 0; i < 40; i++) {
            Circle s = new Circle(rnd.nextDouble() * W, rnd.nextDouble() * 70,
                    0.6 + rnd.nextDouble(), Color.rgb(255, 255, 255, 0.4 + rnd.nextDouble() * 0.5));
            pane.getChildren().add(s);
        }

        // sun glow
        Circle sun = new Circle(640, 125, 34, Color.web("#ffd27a"));
        sun.setEffect(new DropShadow(45, Color.web("#ffb347")));
        pane.getChildren().add(sun);

        // far mountains and near hills
        Polygon far = new Polygon(0, 165, 90, 95, 170, 140, 260, 80, 360, 150, 450, 100,
                560, 150, 660, 90, 800, 150, 800, 165);
        far.setFill(Color.web("#1f3f66", 0.85));
        Polygon near = new Polygon(0, 165, 120, 130, 260, 155, 400, 125, 560, 155,
                700, 130, 800, 155, 800, 165);
        near.setFill(Color.web("#12293f"));
        pane.getChildren().addAll(far, near);

        // ground, ballast, sleepers, rails
        pane.getChildren().add(rect(0, 160, W, 50, "#0c1722", 0));
        pane.getChildren().add(rect(0, 160, W, 8, "#5b6470", 0));
        for (int x = 0; x < W; x += 22) {
            pane.getChildren().add(rect(x, 168, 12, 5, "#3b2f2a", 0));
        }
        pane.getChildren().add(rect(0, 158, W, 3, "#c0c7cf", 0));

        // the train: two coaches and a power car, facing right
        Group train = new Group();
        coach(train, 0);
        coach(train, 232);
        headCar(train, 464);

        // soft shadow under the whole train
        train.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));
        pane.getChildren().add(train);

        TranslateTransition move = new TranslateTransition(Duration.seconds(14), train);
        move.setFromX(-800);
        move.setToX(W + 10);
        move.setInterpolator(Interpolator.LINEAR);
        move.setCycleCount(Animation.INDEFINITE);
        move.play();

        return pane;
    }

    private static LinearGradient bodyPaint() {
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffffff")), new Stop(1, Color.web("#c9d3df")));
    }

    private static LinearGradient glassPaint() {
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1b4a80")), new Stop(1, Color.web("#0a2342")));
    }

    private static void coach(Group g, double x) {
        Rectangle body = new Rectangle(x, 112, 226, 44);
        body.setArcWidth(14);
        body.setArcHeight(14);
        body.setFill(bodyPaint());
        g.getChildren().add(body);

        g.getChildren().add(rect(x, 138, 226, 7, "#1565c0", 0));    // blue stripe
        g.getChildren().add(rect(x, 146, 226, 2, "#f2a900", 0));    // gold line

        for (int i = 0; i < 6; i++) {                                // windows
            Rectangle w = new Rectangle(x + 14 + i * 34, 120, 24, 14);
            w.setArcWidth(5);
            w.setArcHeight(5);
            w.setFill(glassPaint());
            g.getChildren().add(w);
        }
        wheels(g, x, 226);
    }

    private static void headCar(Group g, double x) {
        Polygon body = new Polygon(x, 112, x + 150, 112, x + 215, 134, x + 230, 152,
                x + 230, 156, x, 156);
        body.setFill(bodyPaint());
        g.getChildren().add(body);

        g.getChildren().add(rect(x, 138, 210, 7, "#1565c0", 0));
        g.getChildren().add(rect(x, 146, 218, 2, "#f2a900", 0));

        for (int i = 0; i < 4; i++) {
            Rectangle w = new Rectangle(x + 14 + i * 34, 120, 24, 14);
            w.setArcWidth(5);
            w.setArcHeight(5);
            w.setFill(glassPaint());
            g.getChildren().add(w);
        }

        Polygon shield = new Polygon(x + 150, 116, x + 150, 130, x + 196, 130);   // windscreen
        shield.setFill(glassPaint());
        g.getChildren().add(shield);

        // headlight and light beam
        Polygon beam = new Polygon(x + 230, 148, x + 340, 136, x + 340, 164);
        beam.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 243, 176, 0.55)),
                new Stop(1, Color.rgb(255, 243, 176, 0))));
        g.getChildren().add(beam);
        Circle lamp = new Circle(x + 226, 148, 3.5, Color.web("#fff3b0"));
        lamp.setEffect(new DropShadow(12, Color.web("#ffe680")));
        g.getChildren().add(lamp);

        wheels(g, x, 226);
    }

    private static void wheels(Group g, double x, double w) {
        double[] pos = {30, 52, w - 52, w - 30};
        for (double p : pos) {
            g.getChildren().add(new Circle(x + p, 157, 5.5, Color.web("#1b1f24")));
            g.getChildren().add(new Circle(x + p, 157, 2, Color.web("#9aa5b1")));
        }
    }

    private static Rectangle rect(double x, double y, double w, double h, String color, double arc) {
        Rectangle r = new Rectangle(x, y, w, h);
        r.setFill(Color.web(color));
        r.setArcWidth(arc);
        r.setArcHeight(arc);
        return r;
    }
}