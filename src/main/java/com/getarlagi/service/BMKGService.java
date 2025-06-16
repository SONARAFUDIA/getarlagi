package com.getarlagi.service;

import com.fasterxml.jackson.databind.DeserializationFeature; // <-- Tambahkan import ini
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.getarlagi.model.bmkg.BMKGData;
import com.getarlagi.model.bmkg.Gempa;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BMKGService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
            
    // Modifikasi pada bagian ini
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            // BARIS INI ADALAH SOLUSINYA:
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String BMKG_API_URL = "https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json";

    public Gempa getLatestEarthquake() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BMKG_API_URL))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            BMKGData data = objectMapper.readValue(response.body(), BMKGData.class);
            if (data != null && data.getInfoGempa() != null) {
                return data.getInfoGempa().getGempa();
            } else {
                 throw new RuntimeException("Format data JSON dari BMKG tidak sesuai harapan.");
            }
        } else {
            throw new RuntimeException("Gagal mengambil data dari BMKG. Status code: " + response.statusCode());
        }
    }
}