module parkflow.deskoptworker {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens parkflow.deskoptworker to javafx.fxml;
    exports parkflow.deskoptworker;
    exports parkflow.deskoptworker.Controllers;
    opens parkflow.deskoptworker.Controllers to javafx.fxml;
    exports parkflow.deskoptworker.Controllers.Admin;
    opens parkflow.deskoptworker.Controllers.Admin to javafx.fxml;
    exports parkflow.deskoptworker.Controllers.Worker;
    opens parkflow.deskoptworker.Controllers.Worker to javafx.fxml;
    exports parkflow.deskoptworker.Controllers.sharedPanels;
    opens parkflow.deskoptworker.Controllers.sharedPanels to javafx.fxml;
    opens parkflow.deskoptworker.models to javafx.fxml, javafx.base;
    exports parkflow.deskoptworker.models;
    exports parkflow.deskoptworker.Controllers.Components;
    opens parkflow.deskoptworker.Controllers.Components to javafx.fxml;
    exports parkflow.deskoptworker.Controllers.sharedPanels.Reports;
    opens parkflow.deskoptworker.Controllers.sharedPanels.Reports to javafx.fxml;
    exports parkflow.deskoptworker.utils;

    exports parkflow.deskoptworker.dto;
    opens parkflow.deskoptworker.dto to com.fasterxml.jackson.databind;

    opens parkflow.deskoptworker.api to com.fasterxml.jackson.databind;
}