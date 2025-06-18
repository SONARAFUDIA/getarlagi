package com.getarlagi.controller;

import com.getarlagi.model.Aftershock;
import com.getarlagi.model.AftershockCalculator;
import com.getarlagi.model.Mainshock;
import com.getarlagi.model.bmkg.Gempa;
import com.getarlagi.service.BMKGService;
import com.getarlagi.util.DistanceCalculator;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

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
    @FXML private WebView mapWebView;
    @FXML private Button fetchBMKGButton;
    @FXML private Label bmkgStatusLabel;

    private WebEngine webEngine;
    private final AftershockCalculator calculator = new AftershockCalculator();
    private final BMKGService bmkgService = new BMKGService();
    private JavaBridge javaBridge;


    @FXML
    public void initialize() {
        if (mapWebView != null) {
            webEngine = mapWebView.getEngine();
            javaBridge = new JavaBridge();
            webEngine.setJavaScriptEnabled(true);

            try {
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

            webEngine.getLoadWorker().stateProperty().addListener(
                (obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaApp", javaBridge);
                        System.out.println("Bridge Java-JS 'javaApp' telah disiapkan.");
                    } else if (newState == Worker.State.FAILED) {
                        System.err.println("Gagal memuat halaman peta di WebView.");
                        webEngine.loadContent("<html><body><h1>Gagal memuat peta.</h1><p>Pastikan Anda memiliki koneksi internet dan file peta ada.</p></body></html>");
                    }
                });

        } else {
            System.err.println("mapWebView belum diinisialisasi. Periksa FXML Anda.");
        }
    }

    public class JavaBridge {
        public void handleMapClick(double lat, double lon) {
            Platform.runLater(() -> {
                mainshockLatitudeField.setText(String.format(Locale.US, "%.4f", lat));
                mainshockLongitudeField.setText(String.format(Locale.US, "%.4f", lon));
                System.out.println("Peta diklik di Java: Lat: " + lat + ", Lon: " + lon);
            });
        }
        public void log(String message) {
            System.out.println("Pesan dari JavaScript: " + message);
        }
    }
    
    @FXML
    private void handleFetchBMKGButtonAction(ActionEvent event) {
        if(bmkgStatusLabel != null) bmkgStatusLabel.setText("Mengambil data dari BMKG...");

        new Thread(() -> {
            try {
                Gempa gempa = bmkgService.getLatestEarthquake();

                String lintangStr = gempa.getLintang();
                String bujurStr = gempa.getBujur();
                
                String cleanedLintang = lintangStr.replaceAll("[^\\d.-]", "");
                String cleanedBujur = bujurStr.replaceAll("[^\\d.-]", "");
                
                // === BAGIAN YANG DIPERBAIKI ===
                // Inisialisasi 'latitude' hanya satu kali menggunakan operator ternary
                // Ini membuatnya "effectively final"
                double latitude = Double.parseDouble(cleanedLintang) * (lintangStr.contains("LS") ? -1.0 : 1.0);
                double longitude = Double.parseDouble(cleanedBujur);
                double magnitude = Double.parseDouble(gempa.getMagnitude());
                // =============================

                Platform.runLater(() -> {
                    mainshockMagnitudeField.setText(String.format(Locale.US, "%.2f", magnitude));
                    mainshockLatitudeField.setText(String.format(Locale.US, "%.4f", latitude));
                    mainshockLongitudeField.setText(String.format(Locale.US, "%.4f", longitude));
                    if(bmkgStatusLabel != null) bmkgStatusLabel.setText("Data berhasil diambil: M " + magnitude + " di " + gempa.getWilayah());

                     if (webEngine != null && webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                         webEngine.executeScript(String.format(Locale.US, "setMapView(%f, %f, 8);", latitude, longitude));
                     }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Gagal Mengambil Data", "Tidak dapat mengambil data gempa dari BMKG. Periksa koneksi internet Anda.\nError: " + e.getMessage());
                    if(bmkgStatusLabel != null) bmkgStatusLabel.setText("Status: Gagal mengambil data.");
                });
            }
        }).start();
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

            aftershockListView.getItems().clear();
            if (predictedAftershocks.isEmpty()) {
                aftershockListView.getItems().add("Tidak ada gempa susulan diprediksi.");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                for (Aftershock as : predictedAftershocks) {
                    aftershockListView.getItems().add(String.format(Locale.US,
                        "Mag: %.2f, Lintang: %.4f, Bujur: %.4f, Est. Jarak: %.2f km, Waktu: %s",
                        as.getMagnitude(), as.getLatitude(), as.getLongitude(),
                        as.getMainshockDistanceKm(), as.getTimestamp().format(formatter)
                    ));
                }
            }

            if (webEngine != null && webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                webEngine.executeScript("clearMapElements();");
                webEngine.executeScript(String.format(Locale.US, "addMainshockMarker(%f, %f, %f);",
                    mainshock.getLatitude(), mainshock.getLongitude(), mainshock.getMagnitude()));

                for (Aftershock as : predictedAftershocks) {
                    webEngine.executeScript(String.format(Locale.US, "addAftershockMarker(%f, %f, %f, %f);",
                        as.getLatitude(), as.getLongitude(), as.getMagnitude(), as.getMainshockDistanceKm()));
                }

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