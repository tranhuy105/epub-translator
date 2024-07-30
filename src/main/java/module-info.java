module com.tranhuy105.epubtranslator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.web;
    requires epublib.core;
    requires org.jsoup;
    requires jdk.jsobject;
    requires com.google.gson;

    opens com.tranhuy105.epubtranslator to javafx.fxml, com.google.gson;
    opens com.tranhuy105.epubtranslator.models to com.google.gson, javafx.fxml;
    opens com.tranhuy105.epubtranslator.services to com.google.gson, javafx.fxml;
    opens com.tranhuy105.epubtranslator.controllers to com.google.gson, javafx.fxml;
    exports com.tranhuy105.epubtranslator;
    exports com.tranhuy105.epubtranslator.models;
    exports com.tranhuy105.epubtranslator.services;
    exports com.tranhuy105.epubtranslator.controllers;
}