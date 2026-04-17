
module org.example.kyrcah {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.desktop;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jdk.compiler;
    requires org.junit.jupiter.api;
    opens org.example.kyrcah to javafx.fxml, org.junit.platform.commons;
    exports org.example.kyrcah;
}