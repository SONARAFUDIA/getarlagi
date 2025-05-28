// GetarLagi/src/main/java/module-info.java
module com.getarlagi { // Nama modul harus sesuai dengan root paket Anda
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires javafx.web;
    requires jdk.jsobject;

    // Gabungkan 'opens' untuk paket controller menjadi satu baris
    opens com.getarlagi.controller to javafx.fxml, javafx.web;

    // Penting: Buka paket utama tempat GetarLagiApp berada
    opens com.getarlagi to javafx.fxml;

    // Ekspor paket utama agar JVM bisa menemukan kelas utama
    exports com.getarlagi;
}