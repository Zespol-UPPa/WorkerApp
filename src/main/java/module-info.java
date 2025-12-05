module parkflow.deskoptworker {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;

    opens parkflow.deskoptworker to javafx.fxml;
    exports parkflow.deskoptworker;
}