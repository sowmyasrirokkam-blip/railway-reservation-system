import javafx.scene.layout.Region;

public interface Page {
    Region getRoot();
    void refresh();
}