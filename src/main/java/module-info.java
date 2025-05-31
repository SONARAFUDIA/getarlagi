module com.getarlagi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires javafx.web;
    requires jdk.jsobject;

    opens com.getarlagi.controller to javafx.fxml, javafx.web;

    opens com.getarlagi to javafx.fxml;

    exports com.getarlagi;
}