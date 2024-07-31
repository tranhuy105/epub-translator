package com.tranhuy105.epubtranslator.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tranhuy105.epubtranslator.models.RecentFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RecentFilesManager {
    private static final String RECENT_FILES_SAVE_PATH = System.getProperty("user.home") + "/.epubtranslator/recent_files.json";
    private final List<RecentFile> recentFiles;
    private static RecentFilesManager instance;

    private RecentFilesManager() {
        recentFiles = loadRecentFiles();
    }

    public static RecentFilesManager getInstance() {
        if (instance == null) {
            instance = new RecentFilesManager();
        }
        return instance;
    }

    public List<RecentFile> getRecentFiles() {
        return recentFiles;
    }

    public void addRecentFile(RecentFile file) {
        recentFiles.removeIf(f -> f.getFilePath().equals(file.getFilePath()));
        recentFiles.add(0, file);
        saveRecentFiles();
    }

    public void removeRecentFile(String filepath) {
        recentFiles.removeIf(f -> f.getFilePath().equals(filepath));
        saveRecentFiles();
    }

    private List<RecentFile> loadRecentFiles() {
        try {
            Path path = getRecentFilePath();
            if (Files.exists(path)) {
                String json = Files.readString(path);
                return new Gson().fromJson(json, new TypeToken<List<RecentFile>>() {}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void saveRecentFiles() {
        try {
            Path path = getRecentFilePath();
            Files.createDirectories(path.getParent());
            String json = new Gson().toJson(recentFiles);
            Files.writeString(path, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getRecentFilePath() {
        return Paths.get(RECENT_FILES_SAVE_PATH);
    }
}
