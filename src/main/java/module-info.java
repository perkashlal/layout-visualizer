module layoutvisualizer {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires jakarta.xml.bind;
    requires transitive javafx.graphics;

    opens layoutvisualizer to javafx.fxml, jakarta.xml.bind;
    exports layoutvisualizer;
    opens layoutvisualizer.model to javafx.fxml, jakarta.xml.bind;
    exports layoutvisualizer.model;
    opens layoutvisualizer.model.network to jakarta.xml.bind;
    exports layoutvisualizer.model.network;
}
