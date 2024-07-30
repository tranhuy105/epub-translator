package com.tranhuy105.epubtranslator.services;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslationTaskManager {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(6);

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}
