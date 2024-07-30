package com.tranhuy105.epubtranslator.models;

import javafx.concurrent.Task;
import java.util.HashMap;
import java.util.Map;

public class TranslationTask extends Task<String> {
    private final String originalText;
    private final String source = "ja";
    private final String target = "en";
    private static final Map<String, String> cache = new HashMap<>();
    private final ApiClient apiClient;

    public TranslationTask(String originalText, ApiClient apiClient) {
        this.originalText = originalText;
        this.apiClient = apiClient;
    }

    @Override
    protected String call() throws Exception {
        String cacheKey = source + ":" + target + ":" + originalText;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        String translatedText = apiClient.translate(originalText, source, target);
        cache.put(cacheKey, translatedText);
        return translatedText;
    }
}
