package com.tranhuy105.epubtranslator.models;

public class UserPreferences {
    private String microsoftTextTranslatorApiKey = "";
    private String deepTranslatorApiKey = "";
    private int preferredTextSize = 32; // Default value
    private String preferredMode = "Light"; // Default value
    private Language preferredSourceLanguage = Language.JAPANESE; // Default value
    private Language preferredTargetLanguage = Language.ENGLISH; // Default value

    public String getMicrosoftTextTranslatorApiKey() {
        return microsoftTextTranslatorApiKey;
    }

    public void setMicrosoftTextTranslatorApiKey(String microsoftTextTranslatorApiKey) {
        this.microsoftTextTranslatorApiKey = microsoftTextTranslatorApiKey;
    }

    public String getDeepTranslatorApiKey() {
        return deepTranslatorApiKey;
    }

    public void setDeepTranslatorApiKey(String deepTranslatorApiKey) {
        this.deepTranslatorApiKey = deepTranslatorApiKey;
    }

    public int getPreferredTextSize() {
        return preferredTextSize;
    }

    public void setPreferredTextSize(int preferredTextSize) {
        this.preferredTextSize = preferredTextSize;
    }

    public String getPreferredMode() {
        return preferredMode;
    }

    public void setPreferredMode(String preferredMode) {
        this.preferredMode = preferredMode;
    }

    public Language getPreferredSourceLanguage() {
        return preferredSourceLanguage;
    }

    public void setPreferredSourceLanguage(Language preferredSourceLanguage) {
        this.preferredSourceLanguage = preferredSourceLanguage;
    }

    public Language getPreferredTargetLanguage() {
        return preferredTargetLanguage;
    }

    public void setPreferredTargetLanguage(Language preferredTargetLanguage) {
        this.preferredTargetLanguage = preferredTargetLanguage;
    }
}
