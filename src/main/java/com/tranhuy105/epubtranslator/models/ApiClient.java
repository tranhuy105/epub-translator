package com.tranhuy105.epubtranslator.models;

import com.tranhuy105.epubtranslator.services.ApiClientService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public interface ApiClient {
    default String translate(String originalText, String source, String target) throws Exception {
        URL url = buildUrl(originalText, source, target);
        HttpURLConnection conn = getHttpURLConnection(originalText, url, source, target);
        return ApiClientService.handleResponse(conn, this::parseResponse);
    }
    HttpURLConnection getHttpURLConnection(String originalText, URL url, String source, String target) throws IOException;
    URL buildUrl(String originalText, String source, String target) throws MalformedURLException;
    String parseResponse(String content);
}
