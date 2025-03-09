module org.client {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires exchange;
    requires static lombok;
    requires java.sql;

    opens org.client to java.base, javafx.fxml, javafx.graphics, javafx.controls;
    opens org.client.controller to javafx.base, javafx.fxml, javafx.graphics, javafx.controls;
    opens org.client.utils to java.base, javafx.controls, javafx.fxml, javafx.graphics;
//    exports org.server;
//    exports org.server.controller;
}