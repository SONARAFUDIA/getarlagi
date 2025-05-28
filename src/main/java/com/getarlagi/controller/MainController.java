package com.getarlagi.controller;

import com.getarlagi.model.Aftershock;
import com.getarlagi.model.AftershockCalculator;
import com.getarlagi.model.Mainshock;
import com.getarlagi.util.DistanceCalculator;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject; // Untuk komunikasi Java-JS

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML private TextField mainshockMagnitudeField;
    @FXML private TextField mainshockLatitudeField;
    @FXML private TextField mainshockLongitudeField;
    @FXML private TextField predictionDurationHoursField;
    @FXML private TextField minAftershockMagnitudeField;
    @FXML private Button calculateButton;
    @FXML private ListView<String> aftershockListView;

    @FXML private WebView mapWebView; // Ganti ImageView dan Pane dengan WebView
    private WebEngine webEngine;

    private AftershockCalculator calculator = new AftershockCalculator();

    // Objek untuk menjembatani Java ke JavaScript
    private JavaBridge javaBridge;


    @FXML
    public void initialize() {
        if (mapWebView != null) {
            webEngine = mapWebView.getEngine();
            javaBridge = new JavaBridge(); // Buat instance bridge

            // Aktifkan JavaScript
            webEngine.setJavaScriptEnabled(true);

            // Muat file HTML peta lokal
            // Pastikan file map.html ada di src/main/resources/com/getarlagi/html/map.html
            try {
                // Cara yang lebih robust untuk mendapatkan URL resource
                java.net.URL mapHtmlUrl = getClass().getResource("/com/getarlagi/html/map.html");
                if (mapHtmlUrl != null) {
                    webEngine.load(mapHtmlUrl.toExternalForm());
                } else {
                    System.err.println("File map.html tidak ditemukan di resources.");
                    showAlert("Error Peta", "File konfigurasi peta (map.html) tidak ditemukan.");
                }
            } catch (Exception e) {
                System.err.println("Gagal memuat map.html: " + e.getMessage());
                showAlert("Error Peta", "Gagal memuat file konfigurasi peta: " + e.getMessage());
            }


            // Siapkan komunikasi dari JavaScript ke Java
            webEngine.getLoadWorker().stateProperty().addListener(
                (obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        // Dapatkan window object dari JavaScript
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        // Ekspos objek Java (javaBridge) ke JavaScript dengan nama "javaApp"
                        window.setMember("javaApp", javaBridge);
                        System.out.println("Bridge Java-JS 'javaApp' telah disiapkan.");

                        // Bisa panggil fungsi JS untuk inisialisasi peta jika perlu
                        // webEngine.executeScript("initializeMapIfNeeded();");
                    } else if (newState == Worker.State.FAILED) {
                        System.err.println("Gagal memuat halaman peta di WebView.");
                        webEngine.loadContent("<html><body><h1>Gagal memuat peta.</h1><p>Pastikan Anda memiliki koneksi internet dan kunci API Geoapify sudah benar di map.html.</p></body></html>");
                    }
                });

        } else {
            System.err.println("mapWebView belum diinisialisasi. Periksa FXML Anda.");
        }
    }

    /**
     * Kelas inner untuk menjembatani panggilan dari JavaScript ke Java.
     * Metode dalam kelas ini yang ber-modifier public bisa dipanggil dari JavaScript.
     */
    public class JavaBridge {
        public void handleMapClick(double lat, double lon) {
            Platform.runLater(() -> {
                // Update field dari thread JavaFX
                mainshockLatitudeField.setText(String.format(Locale.US, "%.4f", lat));
                mainshockLongitudeField.setText(String.format(Locale.US, "%.4f", lon));
                System.out.println("Peta diklik di Java: Lat: " + lat + ", Lon: " + lon);

                // Tampilkan opsi untuk menghitung (bisa pakai ContextMenu atau langsung hitung)
                // Di sini kita contohkan langsung hitung jika field lain sudah terisi (opsional)
                // atau bisa juga tampilkan dialog konfirmasi.
                // Untuk konsistensi, kita akan panggil handleCalculateButtonAction jika data valid
                // Untuk contoh ini, kita biarkan pengguna menekan tombol "Hitung" secara manual setelah Lat/Lon terisi
                // Atau, bisa tampilkan ContextMenu JavaFX (lebih rumit karena perlu koordinat layar global)
                // Untuk kesederhanaan, kita asumsikan pengguna akan menekan tombol "Hitung".

                // Jika ingin ContextMenu JavaFX, perlu cara mendapatkan koordinat layar dari klik WebView,
                // yang bisa jadi rumit. Cara lebih mudah adalah membuat tombol/opsi di dalam HTML itu sendiri
                // yang memanggil fungsi JS lain, lalu fungsi JS itu memanggil metode Java lain.
            });
        }

        // Anda bisa menambahkan metode lain di sini yang ingin dipanggil dari JavaScript
        public void log(String message) {
            System.out.println("Pesan dari JavaScript: " + message);
        }
    }


    @FXML
    private void handleCalculateButtonAction(ActionEvent event) {
        try {
            double mainshockMagnitude = Double.parseDouble(mainshockMagnitudeField.getText());
            double mainshockLatitude = Double.parseDouble(mainshockLatitudeField.getText());
            double mainshockLongitude = Double.parseDouble(mainshockLongitudeField.getText());
            double durationHours = Double.parseDouble(predictionDurationHoursField.getText());
            double minMagnitude = Double.parseDouble(minAftershockMagnitudeField.getText());

            if (mainshockMagnitude <= 0 || mainshockMagnitude > 10 ||
                mainshockLatitude < -90 || mainshockLatitude > 90 ||
                mainshockLongitude < -180 || mainshockLongitude > 180 ||
                durationHours <= 0 || minMagnitude < 0 || minMagnitude > mainshockMagnitude) {
                showAlert("Input Tidak Valid", "Harap periksa kembali nilai input Anda.");
                return;
            }

            Mainshock mainshock = new Mainshock(
                mainshockLatitude,
                mainshockLongitude,
                mainshockMagnitude,
                LocalDateTime.now(),
                "Mainshock Location (Input)",
                "Unknown Tectonic Plate"
            );

            List<Aftershock> predictedAftershocks = calculator.calculateAftershocks(mainshock, durationHours, minMagnitude);

            // Update ListView
            aftershockListView.getItems().clear();
            if (predictedAftershocks.isEmpty()) {
                aftershockListView.getItems().add("Tidak ada gempa susulan diprediksi.");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                for (Aftershock as : predictedAftershocks) {
                    aftershockListView.getItems().add(String.format(Locale.US,
                        "Mag: %.2f, Lat: %.4f, Lon: %.4f, Est. Dist: %.2f km, Time: %s",
                        as.getMagnitude(), as.getLatitude(), as.getLongitude(),
                        as.getMainshockDistanceKm(), as.getTimestamp().format(formatter)
                    ));
                }
            }

            // Perbarui peta di WebView
            if (webEngine != null && webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                // Panggil fungsi JavaScript untuk membersihkan marker lama dan menambah yang baru
                webEngine.executeScript("clearMapElements();");
                webEngine.executeScript(String.format(Locale.US, "addMainshockMarker(%f, %f, %f);",
                    mainshock.getLatitude(), mainshock.getLongitude(), mainshock.getMagnitude()));

                for (Aftershock as : predictedAftershocks) {
                    webEngine.executeScript(String.format(Locale.US, "addAftershockMarker(%f, %f, %f, %f);",
                        as.getLatitude(), as.getLongitude(), as.getMagnitude(), as.getMainshockDistanceKm()));
                }

                // Gambar lingkaran estimasi jarak
                double estimatedRuptureZoneKm = DistanceCalculator.estimateDistanceKm(mainshock.getMagnitude());
                webEngine.executeScript(String.format(Locale.US, "drawDistanceCircle(%f, %f, %f);",
                    mainshock.getLatitude(), mainshock.getLongitude(), estimatedRuptureZoneKm));
            }


        } catch (NumberFormatException e) {
            showAlert("Input Error", "Pastikan semua field diisi dengan angka yang valid.");
        } catch (Exception e) {
            showAlert("Terjadi Kesalahan", "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}