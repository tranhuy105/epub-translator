package com.tranhuy105.epubtranslator.models;

import javafx.concurrent.Task;

public class TranslationTask extends Task<String> {
    private final String originalText;
    private final String source = "jp";
    private final String target = "en";

    public TranslationTask(String originalText) {
        this.originalText = originalText;
    }

    @Override
    protected String call() throws Exception {
        String apiUrl = "https://api.example.com/translate?text=" + originalText;
//        Thread.sleep(2000); // Simulated delay
        return "Translated: " + originalText;
    }
}
