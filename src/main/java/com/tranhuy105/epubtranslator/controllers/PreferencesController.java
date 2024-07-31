package com.tranhuy105.epubtranslator.controllers;

import com.tranhuy105.epubtranslator.models.Language;
import com.tranhuy105.epubtranslator.models.UserPreferences;
import com.tranhuy105.epubtranslator.services.UserPreferencesManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class PreferencesController {

    @FXML
    private TextField microsoftTranslatorApiKeyField;
    @FXML
    private TextField deepTranslatorApiKeyField;

    @FXML
    private TextField textSizeField;

    @FXML
    private ComboBox<String> modeComboBox;

    @FXML
    private ComboBox<Language> sourceLanguageComboBox;

    @FXML
    private ComboBox<Language> targetLanguageComboBox;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        modeComboBox.getItems().addAll("Light", "Dark");
        sourceLanguageComboBox.getItems().addAll(Language.values());
        targetLanguageComboBox.getItems().addAll(Language.values());

        // Load existing preferences
        UserPreferences preferences = UserPreferencesManager.getInstance().getUserPreferences();
        microsoftTranslatorApiKeyField.setText(preferences.getMicrosoftTextTranslatorApiKey());
        deepTranslatorApiKeyField.setText(preferences.getDeepTranslatorApiKey());
        textSizeField.setText(String.valueOf(preferences.getPreferredTextSize()));
        modeComboBox.setValue(preferences.getPreferredMode());
        sourceLanguageComboBox.setValue(preferences.getPreferredSourceLanguage());
        targetLanguageComboBox.setValue(preferences.getPreferredTargetLanguage());
    }

    @FXML
    protected void saveUserPreferences() {

        UserPreferences preferences = getUserPreferences();
        if (preferences != null) {
            UserPreferencesManager.getInstance().setUserPreferences(preferences);
            showAlert("Success", "User preferences have been saved successfully.");
            closeDialog();
        }
    }

    private UserPreferences getUserPreferences() {
        int textSize;
        try {
            textSize = Integer.parseInt(textSizeField.getText());
            if (textSize < 16 || textSize > 48) {
                showAlert("Invalid Input", "Text size must be between 16 and 48");
                return null;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Text size must be a number.");
            return null;
        }
        String mode = modeComboBox.getValue();
        Language sourceLanguage = sourceLanguageComboBox.getValue();
        Language targetLanguage = targetLanguageComboBox.getValue();
        String deepTranslatorApiKey = deepTranslatorApiKeyField.getText();
        String microsoftTranslatorApiKey = microsoftTranslatorApiKeyField.getText();

        UserPreferences preferences = UserPreferencesManager.getInstance().getUserPreferences();
        preferences.setDeepTranslatorApiKey(deepTranslatorApiKey);
        preferences.setMicrosoftTextTranslatorApiKey(microsoftTranslatorApiKey);
        preferences.setPreferredTextSize(textSize);
        preferences.setPreferredMode(mode);
        preferences.setPreferredSourceLanguage(sourceLanguage);
        preferences.setPreferredTargetLanguage(targetLanguage);
        return preferences;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
