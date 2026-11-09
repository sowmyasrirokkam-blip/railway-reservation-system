
import javafx.animation.ScaleTransition;
import java.io.File;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class HeroScene {

    public static final double W = 1150, H = 740;

    public static Pane create() {
        Pane p = new Pane();
        p.setPrefSize(W, H);
        p.setMinSize(W, H);
        p.setMaxSize(W, H);
        p.setClip(new Rectangle(W, H));

        // optional: if a file named hero.jpg is next to the app, use the real photo
        File photo = new File("hero.jpg");
        if (photo.exists()) {
            Image img = new Image(photo.toURI().toString());
            ImageView iv = new ImageView(img);

            // fill the whole window without stretching the photo
            double scale = Math.max(W / img.getWidth(), H / img.getHeight());
            iv.setFitWidth(img.getWidth() * scale);
            iv.setFitHeight(img.getHeight() * scale);
            iv.setLayoutX((W - img.getWidth() * scale) / 2);
            iv.setLayoutY((H - img.getHeight() * scale) / 2);
            p.getChildren().add(iv);

            // slow cinematic zoom, in and out
            ScaleTransition zoom = new ScaleTransition(Duration.seconds(18), iv);
            zoom.setFromX(1.0);
            zoom.setFromY(1.0);
            zoom.setToX(1.10);
            zoom.setToY(1.10);
            zoom.setAutoReverse(true);
            zoom.setCycleCount(Animation.INDEFINITE);
            zoom.setInterpolator(Interpolator.EASE_BOTH);
            zoom.play();
        } else {
            drawForestAndTrain(p);
        }

        // dark shade on the left so the white text is easy to read
        Rectangle shade = new Rectangle(W, H);
        shade.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 0, 0, 0.78)),
                new Stop(0.55, Color.rgb(0, 0, 0, 0.38)),
                new Stop(1, Color.rgb(0, 0, 0, 0.10))));
        shade.setMouseTransparent(true);
        p.getChildren().add(shade);
        return p;
    }

    private static void drawForestAndTrain(Pane p) {
        // misty sky
        Rectangle sky = new Rectangle(W, H);
        sky.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0b2420")),
                new Stop(0.55, Color.web("#2f5d4d")),
                new Stop(1, Color.web("#0b1d18"))));
        p.getChildren().add(sky);

        // four layers of trees, far (light, small) to near (dark, big)
        Random r = new Random(11);
        String[] shades = {"#2d6a4f", "#1f5a43", "#174836", "#0f3528"};
        for (int layer = 0; layer < 4; layer++) {
            double baseY = 250 + layer * 85;
            for (int i = 0; i < 55; i++) {
                double rad = 34 + layer * 12 + r.nextDouble() * 38;
                Circle c = new Circle(r.nextDouble() * (W + 100) - 50,
                        baseY + r.nextDouble() * 70, rad, Color.web(shades[layer]));
                c.setOpacity(0.92);
                p.getChildren().add(c);
            }
        }

        // curved track, going from the right edge down to the left
        Path track = new Path(
                new MoveTo(W + 80, 300),
                new CubicCurveTo(930, 300, 1020, 560, 640, 640),
                new CubicCurveTo(420, 690, 200, 700, -100, 730));

        Path bank = copy(track);                       // soft embankment under the rails
        bank.setStroke(Color.web("#2b3a2e"));
        bank.setStrokeWidth(46);
        bank.setStrokeLineCap(StrokeLineCap.BUTT);
        Path sleepers = copy(track);
        sleepers.setStroke(Color.web("#6b5646"));
        sleepers.setStrokeWidth(20);
        sleepers.getStrokeDashArray().addAll(4.0, 11.0);
        Path rail = copy(track);
        rail.setStroke(Color.web("#b8c0c8"));
        rail.setStrokeWidth(3);
        p.getChildren().addAll(bank, sleepers, rail);

        // the train: engine plus 6 coaches, each follows the same path
        int cars = 7;
        double secondsPerLoop = 16, gap = 1.15;
        for (int i = cars - 1; i >= 0; i--) {
            Group car = makeCar(i == 0);
            car.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.6)));
            p.getChildren().add(car);

            PathTransition pt = new PathTransition(Duration.seconds(secondsPerLoop), track, car);
            pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            pt.setInterpolator(Interpolator.LINEAR);
            pt.setCycleCount(Animation.INDEFINITE);
            pt.play();
            pt.jumpTo(Duration.seconds((cars - i) * gap));   // keeps the coaches in a line
        }
    }

    private static Path copy(Path src) {
        Path c = new Path(src.getElements());
        c.setFill(null);
        return c;
    }

    private static Group makeCar(boolean engine) {
        Group g = new Group();
        Rectangle body = new Rectangle(-37, -12, 74, 24);
        body.setArcWidth(8);
        body.setArcHeight(8);
        body.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(engine ? "#2563eb" : "#3b82f6")),
                new Stop(1, Color.web("#1e3a8a"))));
        g.getChildren().add(body);

        Rectangle stripe = new Rectangle(-37, 4, 74, 3);
        stripe.setFill(Color.web("#fbbf24"));
        g.getChildren().add(stripe);

        int windows = engine ? 3 : 5;
        for (int i = 0; i < windows; i++) {
            Rectangle w = new Rectangle(-32 + i * 12, -8, 8, 8);
            w.setArcWidth(3);
            w.setArcHeight(3);
            w.setFill(Color.web("#dbeafe"));
            g.getChildren().add(w);
        }
        if (engine) {
            Circle lamp = new Circle(37, 0, 3.5, Color.web("#fff3b0"));
            lamp.setEffect(new DropShadow(14, Color.web("#ffe680")));
            g.getChildren().add(lamp);
        }
        return g;
    }
}