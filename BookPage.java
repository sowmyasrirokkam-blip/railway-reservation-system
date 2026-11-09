import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class BookPage implements Page {

    private final ReservationService service;
    private final BorderPane root;

    private final ComboBox<String> trainBox = new ComboBox<>();
    private final TextField nameField = new TextField();
    private final TextField ageField = new TextField();
    private final ComboBox<String> genderBox =
            new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
    private final ComboBox<String> classBox =
            new ComboBox<>(FXCollections.observableArrayList(Ticket.CLASSES));
    private final Label fareLabel = new Label();
    private final Label message = new Label();
    private final VBox previewHolder = new VBox();

    public BookPage(ReservationService service, Consumer<String> go) {
        this.service = service;

        trainBox.setPromptText("Select a train");
        trainBox.setPrefWidth(420);
        PageKit.field(nameField, "Full name", 420);
        PageKit.field(ageField, "Age", 200);
        genderBox.setPromptText("Select");
        genderBox.setPrefWidth(200);
        classBox.setPromptText("Select");
        classBox.setPrefWidth(200);
        classBox.valueProperty().addListener((o, a, b) -> updateFare());
        updateFare();

        Button bookBtn = PageKit.button("Book Ticket", PageKit.BLUE);
        bookBtn.setPrefSize(420, 42);
        bookBtn.setOnAction(e -> book());

        VBox form = new VBox(14,
                PageKit.txt("Passenger details", 20, FontWeight.BOLD, PageKit.TEXT),
                PageKit.labeled("Train", trainBox),
                PageKit.labeled("Passenger name", nameField),
                new HBox(20, PageKit.labeled("Age", ageField), PageKit.labeled("Gender", genderBox)),
                PageKit.labeled("Class", classBox),
                fareLabel, bookBtn, message);
        form.setPadding(new Insets(20));
        form.setPrefWidth(460);
        form.setMinWidth(460);
        form.setMaxHeight(Region.USE_PREF_SIZE);
        PageKit.card(form);

        showPlaceholder();
        previewHolder.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(previewHolder, Priority.ALWAYS);

        root = PageKit.page("Book Ticket", go, new HBox(18, form, previewHolder));
        refresh();
    }

    private void updateFare() {
        String c = classBox.getValue();
        fareLabel.setText(c == null ? "Fare: select a class"
                                    : "Fare: Rs. " + (int) Ticket.fareOf(c));
        fareLabel.setFont(javafx.scene.text.Font.font("Segoe UI", FontWeight.BOLD, 15));
        fareLabel.setTextFill(PageKit.ORANGE);
    }

    private void showPlaceholder() {
        VBox box = new VBox(PageKit.txt("Your ticket will appear here", 16, FontWeight.NORMAL, PageKit.GREY));
        box.setAlignment(Pos.CENTER);
        box.setPrefHeight(300);
        PageKit.card(box);
        previewHolder.getChildren().setAll(box);
    }

    private void book() {
        try {
            if (trainBox.getValue() == null || genderBox.getValue() == null || classBox.getValue() == null) {
                throw new IllegalArgumentException("Please select train, gender and class");
            }
            String trainNo = trainBox.getValue().split(" - ")[0];
            int age = Integer.parseInt(ageField.getText().trim());
            Ticket t = service.bookTicket(trainNo, nameField.getText(), age,
                    genderBox.getValue(), classBox.getValue());
            previewHolder.getChildren().setAll(PageKit.ticketView(service, t));
            nameField.clear();
            ageField.clear();
            refresh();
            message.setText("Ticket " + t.getTicketNo() + " booked successfully!");
            message.setTextFill(PageKit.GREEN);
        } catch (NumberFormatException ex) {
            fail("Age must be a number");
        } catch (NoSeatsAvailableException ex) {
            fail(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            fail(ex.getMessage());
        }
    }

    private void fail(String msg) {
        message.setText(msg);
        message.setTextFill(PageKit.RED);
        PageKit.error(msg);
    }

    // used by the "Buy Now" button on the Trains page
    public void selectTrain(Train t) {
        refresh();
        for (String item : trainBox.getItems()) {
            if (item.startsWith(t.getTrainNo() + " - ")) {
                trainBox.setValue(item);
                break;
            }
        }
    }

    public Region getRoot() { return root; }

    public void refresh() {
        String keep = trainBox.getValue() == null ? null : trainBox.getValue().split(" - ")[0];
        trainBox.getItems().clear();
        for (Train t : service.getAllTrains()) {
            trainBox.getItems().add(t.getTrainNo() + " - " + t.getName()
                    + "  (" + t.getAvailableSeats() + " seats left)");
        }
        if (keep != null) {
            for (String item : trainBox.getItems()) {
                if (item.startsWith(keep + " - ")) trainBox.setValue(item);
            }
        }
    }
}