package com.tranhuy105.epubtranslator.models.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tranhuy105.epubtranslator.models.ApiClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DeepTranslateApiClient implements ApiClient {
    private static final String API_URL = "https://deep-translate1.p.rapidapi.com/language/translate/v2";
    private final String apiKey;

    public DeepTranslateApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public HttpURLConnection getHttpURLConnection(String originalText, URL url, String source, String target) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-rapidapi-host", "deep-translate1.p.rapidapi.com");
        conn.setRequestProperty("x-rapidapi-key", apiKey);
        conn.setDoOutput(true);

        String escapedOriginalText = originalText.replace("\"", "\\\"");
        String jsonInputString = String.format("{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\"}",
                escapedOriginalText, source, target);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException ex) {
            conn.disconnect();
            throw ex;
        }
        return conn;
    }

    @Override
    public URL buildUrl(String originalText, String source, String target) throws MalformedURLException {
        return new URL(API_URL);
    }

    @Override
    public String parseResponse(String content) {
        JsonObject responseObject = JsonParser.parseString(content).getAsJsonObject();
        JsonObject data = responseObject.getAsJsonObject("data");
        JsonObject translations = data.getAsJsonObject("translations");
        return translations.get("translatedText").getAsString();
    }
}
