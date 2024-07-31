package com.tranhuy105.epubtranslator;

import com.tranhuy105.epubtranslator.services.TranslationTaskManager;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(EpubReaderApp.class, args);
        TranslationTaskManager.getExecutorService().shutdownNow();
    }
}
