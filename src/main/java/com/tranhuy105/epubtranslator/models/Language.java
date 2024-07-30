package com.tranhuy105.epubtranslator.models;

public enum Language {
    ENGLISH("en"),
    SPANISH("es"),
    FRENCH("fr"),
    GERMAN("de"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    CHINESE("zh"),
    RUSSIAN("ru"),
    PORTUGUESE("pt"),
    ARABIC("ar"),
    HINDI("hi"),
    TURKISH("tr"),
    SWEDISH("sv"),
    DANISH("da"),
    NORWEGIAN("no"),
    FINNISH("fi"),
    POLISH("pl"),
    DUTCH("nl"),
    GREEK("el"),
    HUNGARIAN("hu"),
    CZECH("cs"),
    ROMANIAN("ro"),
    BULGARIAN("bg"),
    THAI("th"),
    VIETNAMESE("vi"),
    INDONESIAN("id"),
    MALAY("ms"),
    FILIPINO("tl"),
    SWAHILI("sw");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static Language fromCode(String code) {
        for (Language language : values()) {
            if (language.getCode().equalsIgnoreCase(code)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }
}

