package com.tranhuy105.epubtranslator.models;

public class RecentFile {
    private final String filePath;
    private final String title;
    private final String author;
    private final String coverImage;

    public RecentFile(String filePath, String title, String author, String coverImage) {
        this.filePath = filePath;
        this.title = title;
        this.author = author;
        this.coverImage = coverImage;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverImage() {
        return coverImage;
    }
}
