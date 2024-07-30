package com.tranhuy105.epubtranslator.services;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class EpubToHtmlConverter {
    public static String convertEpubToHtml(Book book) throws IOException {
        // Convert images to Base64 and store them in a map
        Map<String, String> base64Images = book.getResources().getAll().stream()
                .filter(resource -> resource.getMediaType().getName().startsWith("image"))
                .collect(Collectors.toMap(
                        Resource::getHref,
                        EpubToHtmlConverter::convertImageToBase64
                ));

        System.out.println("------------");
        System.out.printf("Found %s images in epub:\n", base64Images.size());
        base64Images.keySet().forEach(System.out::println);
        System.out.println("------------");

        // Convert each ePub section to an HTML string and combine them
        StringBuilder htmlContentBuilder = new StringBuilder();
        Collection<Resource> contents = book.getContents();
        for (Resource res : contents) {
            String htmlContent = new String(res.getData(), StandardCharsets.UTF_8);
            htmlContent = embedBase64Images(htmlContent, base64Images);
            htmlContentBuilder.append(htmlContent);
        }

        base64Images.clear();
        return htmlContentBuilder.toString();
    }

    public static String convertImageToBase64(Resource resource) {
        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }
            byte[] imageBytes = outputStream.toByteArray();
            String mimeType = resource.getMediaType().getName();
            if (mimeType.startsWith("image")) {
                mimeType = switch (mimeType) {
                    case "image/jpeg" -> "image/jpeg";
                    case "image/png" -> "image/png";
                    case "image/gif" -> "image/gif";
                    default -> "image/unknown";
                };
                return "data:" + mimeType + ";base64," + encodeBase64(imageBytes);
            } else {
                throw new IllegalArgumentException("Resource is not an image");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private static void embedTableOfContent(Document document) {
        Elements aElements = document.select("a[href]");
        for (Element a : aElements) {
            String src = a.attr("href");
                if (src.contains("#")) {
                    while (src.charAt(0) != '#') {
                        src = src.substring(1);
                    }
                }
                a.attr("href", src);

        }
    }

    private static String embedBase64Images(String htmlContent, Map<String, String> base64Images) {
        Document doc = Jsoup.parse(htmlContent, Parser.xmlParser());
        embedTableOfContent(doc);
        Elements images = doc.select("image");
        String attribute = "xlink:href";
        if (images.isEmpty()) {
            images  = doc.select("img");
            attribute = "src";
        }

        for (Element img : images) {
            String src = img.attr(attribute);
            String base64Image = findBase64Image(src, base64Images);
            if (base64Image != null) {
                img.attr(attribute, base64Image);
            } else {
                System.out.printf("WARNING: fail to replace source of %s with a Base64 String\n", img);
            }
        }

        return doc.outerHtml();
    }

    private static String findBase64Image(String src, Map<String, String> base64Images) {
        Optional<String> base64Image = base64Images.keySet().stream()
                .filter(src::contains)
                .map(base64Images::get)
                .findFirst();

        return base64Image.orElse(null);
    }
}