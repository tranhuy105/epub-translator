package com.tranhuy105.epubtranslator.models.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tranhuy105.epubtranslator.models.ApiClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MyMemoryApiClient implements ApiClient {
    private static final String API_URL = "https://api.mymemory.translated.net/get";

    @Override
    public URL buildUrl(String originalText, String source, String target) throws MalformedURLException {
        String urlEncodedText = URLEncoder.encode(originalText, StandardCharsets.UTF_8);
        String requestUrl = String.format("%s?q=%s&langpair=%s|%s", API_URL, urlEncodedText, source, target);
        return new URL(requestUrl);
    }

    @Override
    public HttpURLConnection getHttpURLConnection(String originalText, URL url, String source, String target) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
    }

    @Override
    public String parseResponse(String content) {
        JsonObject responseObject = JsonParser.parseString(content).getAsJsonObject();
        JsonObject responseData = responseObject.getAsJsonObject("responseData");
        int responseCode = responseObject.get("responseStatus").getAsInt();
        String translatedText = responseData.get("translatedText").getAsString();
        if (responseCode == 200) {
            return translatedText;
        } else {
            throw new RuntimeException("Failed to translate text: " + translatedText);
        }
    }
}
