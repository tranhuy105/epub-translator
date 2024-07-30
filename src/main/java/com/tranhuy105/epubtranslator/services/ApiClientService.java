package com.tranhuy105.epubtranslator.services;

import com.tranhuy105.epubtranslator.models.ApiClient;
import com.tranhuy105.epubtranslator.models.ApiClientType;
import com.tranhuy105.epubtranslator.models.client.DeepTranslateApiClient;
import com.tranhuy105.epubtranslator.models.client.MicrosoftTranslatorApiClient;
import com.tranhuy105.epubtranslator.models.client.MyMemoryApiClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class ApiClientService {
    public static ApiClient getClient(ApiClientType type) {
        return switch (type) {
            case MY_MEMORY -> new MyMemoryApiClient();
            case DEEP_TRANSLATE -> new DeepTranslateApiClient("");
            case MICROSOFT_TRANSLATOR -> new MicrosoftTranslatorApiClient("");
        };
    }

    public static String handleResponse(HttpURLConnection conn, Function<String, String> parser) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        conn.disconnect();
        if (conn.getResponseCode() == 200) {
            return parser.apply(content.toString());
        } else {
            throw new RuntimeException("Failed to get response");
        }
    }
}
