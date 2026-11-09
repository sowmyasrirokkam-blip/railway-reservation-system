import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ReservationService service = new ReservationService();
    private Scene scene;
    private HomePage home;
    private TrainsPage trains;
    private BookPage book;
    private CancelPage cancel;
    private HistoryPage history;
    // "Search Train" on the home page opens the Trains page with From and To filled in
    private void searchTrains(String from, String to) {
        trains.search(from, to);
        go("Trains");
    }
    @Override
    public void start(Stage stage) {
        trains = new TrainsPage(service, this::go, this::buyTrain);
        home = new HomePage(service, this::go, this::searchTrains);
        book = new BookPage(service, this::go);
        cancel = new CancelPage(service, this::go);
        history = new HistoryPage(service, this::go);

        scene = new Scene(home.getRoot(), 1150, 740);
        stage.setScene(scene);
        stage.setTitle("Railway Reservation System");
        stage.setMinWidth(1050);
        stage.setMinHeight(650);
        stage.show();
    }

    // switch page by tab name; always refresh so data is up to date
    private void go(String name) {
        Page p;
        switch (name) {
            case "Trains":        p = trains;  break;
            case "Book Ticket":   p = book;    break;
            case "Cancel Ticket": p = cancel;  break;
            case "History":       p = history; break;
            default:              p = home;
        }
        p.refresh();
        scene.setRoot(p.getRoot());
    }

    // "Buy Now" on a train card opens the booking page with that train selected
    private void buyTrain(Train t) {
        book.selectTrain(t);
        go("Book Ticket");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
