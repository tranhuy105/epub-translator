package com.tranhuy105.epubtranslator.models.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tranhuy105.epubtranslator.models.ApiClient;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MicrosoftTranslatorApiClient implements ApiClient {
    private static final String API_URL = "https://microsoft-translator-text.p.rapidapi.com/translate";
    private final String apiKey;

    public MicrosoftTranslatorApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public HttpURLConnection getHttpURLConnection(String originalText, URL url, String source, String target) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-RapidAPI-Key", apiKey);
        conn.setRequestProperty("X-RapidAPI-Host", "microsoft-translator-text.p.rapidapi.com");
        conn.setDoOutput(true);

        String escapedOriginalText = originalText.replace("\"", "\\\"");
        String jsonInputString = String.format("[{\"Text\":\"%s\"}]", escapedOriginalText);
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
        String requestUrl = String.format("%s?api-version=3.0&profanityAction=NoAction&textType=plain&from=%s&to=%s",
                API_URL, source, target);
        return new URL(requestUrl);
    }

    @Override
    public String parseResponse(String content) {
        JsonArray responseArray = JsonParser.parseString(content).getAsJsonArray();
        JsonObject responseObject = responseArray.get(0).getAsJsonObject();
        JsonArray translations = responseObject.getAsJsonArray("translations");
        return translations.get(0).getAsJsonObject().get("text").getAsString();
    }
}
