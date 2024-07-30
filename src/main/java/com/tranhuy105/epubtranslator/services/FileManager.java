package com.tranhuy105.epubtranslator.services;

import com.tranhuy105.epubtranslator.models.RecentFile;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileManager {
    private static FileManager instance;

    private FileManager() {}

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    public Book readEpubFile(String filePath) throws IOException {
        try (InputStream epubInputStream = Files.newInputStream(Paths.get(filePath))) {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(epubInputStream);

            String title = book.getTitle();
            String author = book.getMetadata().getAuthors().stream()
                    .map(a -> a.getFirstname() + " " + a.getLastname())
                    .collect(Collectors.joining(", "));
            String coverImage = extractCoverImage(book);

            RecentFile recentFile = new RecentFile(filePath, title, author, coverImage);
            RecentFilesManager.getInstance().addRecentFile(recentFile);

            return book;
        }
    }

    private String extractCoverImage(Book book) {
        Resource coverResource = book.getCoverImage();
        if (coverResource != null) {
            return EpubToHtmlConverter.convertImageToBase64(coverResource);
        }
        return "";
    }


    public String extractCssFromBook(Book book) throws IOException {
        StringBuilder cssBuilder = new StringBuilder();
        Collection<Resource> resources = book.getResources().getResourcesByMediaType(MediatypeService.CSS);

        for (Resource resource : resources) {
            cssBuilder.append(new String(resource.getData(), StandardCharsets.UTF_8)).append("\n");
        }
        return cssBuilder.toString();
    }

    public String loadCssFromFile(String filename) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new IOException("css file not found");
            }
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        }
    }

}
