import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private ReservationService service = new ReservationService();
    private TableView<Train> trainTable = new TableView<>();
    private TableView<Ticket> historyTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Trains", buildTrainsTab()),
            new Tab("Book Ticket", buildBookTab()),
            new Tab("Cancel Ticket", buildCancelTab()),
            new Tab("History", buildHistoryTab())
        );
        stage.setScene(new Scene(tabs, 850, 500));
        stage.setTitle("Railway Reservation System");
        stage.show();
    }

    // ---------- Tab 1: Trains ----------
    private VBox buildTrainsTab() {
        addColumn(trainTable, "Train No", "trainNo");
        addColumn(trainTable, "Name", "name");
        addColumn(trainTable, "From", "source");
        addColumn(trainTable, "To", "destination");
        addColumn(trainTable, "Departure", "departureTime");
        addColumn(trainTable, "Total Seats", "totalSeats");
        addColumn(trainTable, "Available", "availableSeats");
        refreshTables();
        VBox box = new VBox(10, trainTable);
        box.setPadding(new Insets(10));
        return box;
    }

    // ---------- Tab 2: Book ----------
    private VBox buildBookTab() {
        ComboBox<String> trainBox = new ComboBox<>();
        for (Train t : service.getAllTrains()) {
            trainBox.getItems().add(t.getTrainNo() + " - " + t.getName());
        }
        TextField nameField = new TextField();
        TextField ageField = new TextField();
        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        ComboBox<String> classBox = new ComboBox<>(FXCollections.observableArrayList("Sleeper", "AC"));
        Button bookBtn = new Button("Book Ticket");
        TextArea ticketArea = new TextArea();
        ticketArea.setEditable(false);
        ticketArea.setStyle("-fx-font-family: monospace;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Train:"), trainBox);
        form.addRow(1, new Label("Name:"), nameField);
        form.addRow(2, new Label("Age:"), ageField);
        form.addRow(3, new Label("Gender:"), genderBox);
        form.addRow(4, new Label("Class:"), classBox);
        form.addRow(5, bookBtn);

        bookBtn.setOnAction(e -> {
            try {
                if (trainBox.getValue() == null || genderBox.getValue() == null || classBox.getValue() == null) {
                    throw new IllegalArgumentException("Please select train, gender and class");
                }
                String trainNo = trainBox.getValue().split(" - ")[0];
                int age = Integer.parseInt(ageField.getText().trim());
                Ticket t = service.bookTicket(trainNo, nameField.getText(), age,
                                              genderBox.getValue(), classBox.getValue());
                ticketArea.setText(service.generateTicketText(t));
                refreshTables();
                nameField.clear();
                ageField.clear();
            } catch (NumberFormatException ex) {
                showError("Age must be a number");
            } catch (NoSeatsAvailableException ex) {
                showError(ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        VBox box = new VBox(15, form, ticketArea);
        box.setPadding(new Insets(15));
        return box;
    }

    // ---------- Tab 3: Cancel ----------
    private VBox buildCancelTab() {
        TextField ticketField = new TextField();
        ticketField.setPromptText("Enter ticket number, e.g. TKT1001");
        Button cancelBtn = new Button("Cancel Ticket");
        Label result = new Label();

        cancelBtn.setOnAction(e -> {
            try {
                service.cancelTicket(ticketField.getText());
                result.setText("Ticket " + ticketField.getText().trim().toUpperCase() + " cancelled. Seat is available again.");
                refreshTables();
                ticketField.clear();
            } catch (TicketNotFoundException ex) {
                showError(ex.getMessage());
            }
        });

        VBox box = new VBox(15, new Label("Ticket number:"), ticketField, cancelBtn, result);
        box.setPadding(new Insets(15));
        return box;
    }

    // ---------- Tab 4: History ----------
    private VBox buildHistoryTab() {
        addColumn(historyTable, "Ticket No", "ticketNo");
        addColumn(historyTable, "Train No", "trainNo");
        addColumn(historyTable, "Seat", "seatNo");
        addColumn(historyTable, "Class", "classType");
        addColumn(historyTable, "Fare", "fare");
        addColumn(historyTable, "Status", "status");
        VBox box = new VBox(10, historyTable);
        box.setPadding(new Insets(10));
        return box;
    }

    // ---------- helpers ----------
    private <T> void addColumn(TableView<T> table, String title, String property) {
        TableColumn<T, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(110);
        table.getColumns().add(col);
    }

    private void refreshTables() {
        trainTable.setItems(FXCollections.observableArrayList(service.getAllTrains()));
        historyTable.setItems(FXCollections.observableArrayList(service.getBookingHistory()));
        trainTable.refresh();
        historyTable.refresh();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}