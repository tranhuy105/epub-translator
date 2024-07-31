package com.tranhuy105.epubtranslator.services;

import com.tranhuy105.epubtranslator.models.ApiClient;
import com.tranhuy105.epubtranslator.models.ApiClientType;
import com.tranhuy105.epubtranslator.models.client.DeepTranslateApiClient;
import com.tranhuy105.epubtranslator.models.client.MicrosoftTranslatorApiClient;
import com.tranhuy105.epubtranslator.models.client.MyMemoryApiClient;
import com.tranhuy105.epubtranslator.services.UserPreferencesManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class ApiClientService {
    public static ApiClient getClient(ApiClientType type) {
        return switch (type) {
            case MY_MEMORY -> new MyMemoryApiClient();
            case DEEP_TRANSLATE -> getDeepTranslatorApiClient();
            case MICROSOFT_TRANSLATOR -> getMicrosoftTranslatorClient();
        };
    }

    public static String handleResponse(HttpURLConnection conn, Function<String, String> parser) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } finally {
            conn.disconnect();
        }
        if (conn.getResponseCode() == 200) {
            return parser.apply(content.toString());
        } else {
            throw new RuntimeException("Api call to translation service response with an error status. Please try again later");
        }
    }

    private static DeepTranslateApiClient getDeepTranslatorApiClient() {
        String key = UserPreferencesManager.getInstance().getUserPreferences().getDeepTranslatorApiKey();
        apiKeyCheck(key);
        return new DeepTranslateApiClient(key);
    }

    private static MicrosoftTranslatorApiClient getMicrosoftTranslatorClient() {
        String key = UserPreferencesManager.getInstance().getUserPreferences().getMicrosoftTextTranslatorApiKey();
        apiKeyCheck(key);
        return new MicrosoftTranslatorApiClient(key);
    }

    private static void apiKeyCheck(String apiKey) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.isBlank()) {
            throw new RuntimeException("Please set your api key at user preferences to use this model");
        }
    }
}
