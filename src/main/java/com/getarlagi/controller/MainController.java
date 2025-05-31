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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject; // Untuk komunikasi Java-JS

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
    private WebEngine webEngine;

    private AftershockCalculator calculator = new AftershockCalculator();

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
                        webEngine.loadContent("<html><body><h1>Gagal memuat peta.</h1><p>Pastikan Anda memiliki koneksi internet dan kunci API Geoapify sudah benar di map.html.</p></body></html>");
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