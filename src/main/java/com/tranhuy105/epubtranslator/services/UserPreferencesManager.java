package com.tranhuy105.epubtranslator.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tranhuy105.epubtranslator.models.UserPreferences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserPreferencesManager {
    private static final String PREFERENCES_SAVE_PATH = System.getProperty("user.home") + "/.epubtranslator/user_preferences.json";
    private static UserPreferencesManager instance;
    private UserPreferences userPreferences;

    private UserPreferencesManager() {
        userPreferences = new UserPreferences();
        loadPreferences();
    }

    public static UserPreferencesManager getInstance() {
        if (instance == null) {
            instance = new UserPreferencesManager();
        }
        return instance;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        savePreferences();
    }

    private void loadPreferences() {
        try {
            Path path = getPreferencesPath();
            if (Files.exists(path)) {
                String json = Files.readString(path);
                Gson gson = new GsonBuilder().serializeNulls().create();
                userPreferences = gson.fromJson(json, UserPreferences.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void savePreferences() {
        try {
            Path path = getPreferencesPath();
            Files.createDirectories(path.getParent());
            String json = new Gson().toJson(userPreferences);
            Files.writeString(path, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getPreferencesPath() {
        return Paths.get(PREFERENCES_SAVE_PATH);
    }
}
