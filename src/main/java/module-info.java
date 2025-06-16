module com.getarlagi {
    // Dependensi JavaFX dan lainnya yang sudah ada
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires javafx.web;
    requires jdk.jsobject;

    // === DEPENDENSI BARU UNTUK JACKSON ===
    // Diperlukan untuk ObjectMapper dan fungsionalitas data-binding
    requires com.fasterxml.jackson.databind;
    // Diperlukan untuk anotasi seperti @JsonProperty
    requires com.fasterxml.jackson.annotation;
    // Diperlukan untuk menangani tipe Java 8 Date/Time (mis. LocalDateTime)
    requires com.fasterxml.jackson.datatype.jsr310;
    // ======================================

    // Buka akses ke controller dan package utama untuk JavaFX
    opens com.getarlagi.controller to javafx.fxml, javafx.web;
    opens com.getarlagi to javafx.fxml;

    // Buka akses ke package model BMKG untuk Jackson
    opens com.getarlagi.model.bmkg to com.fasterxml.jackson.databind;

    exports com.getarlagi;
}