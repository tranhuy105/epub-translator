package com.tranhuy105.epubtranslator.controllers;

import com.tranhuy105.epubtranslator.EpubReaderApp;
import com.tranhuy105.epubtranslator.models.Language;
import com.tranhuy105.epubtranslator.services.*;
import com.tranhuy105.epubtranslator.models.ApiClientType;
import com.tranhuy105.epubtranslator.models.TranslationTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collection;
import java.util.Objects;

public class EpubViewerController {
    @FXML
    private WebView webView;
    @FXML
    private TextField pageInput;
    @FXML
    private ComboBox<Language> sourceLanguage;
    @FXML
    private ComboBox<Language> targetLanguage;
    @FXML
    private Slider textSizeSlider;
    @FXML
    private Label textSizeLabel;
    @FXML
    private Button themeToggleButton;
    @FXML
    private ComboBox<ApiClientType> modelType;
    private WebEngine webEngine;
    private boolean isDarkMode = !Objects.equals(UserPreferencesManager.getInstance().getUserPreferences().getPreferredMode(), "Light");
    private ChangeListener<Worker.State> loadStateListener;


    public void initialize() {
        webEngine = webView.getEngine();

        sourceLanguage.setItems(FXCollections.observableArrayList(Language.values()));
        targetLanguage.setItems(FXCollections.observableArrayList(Language.values()));
        modelType.setItems(FXCollections.observableArrayList(ApiClientType.values()));

        sourceLanguage.setValue(UserPreferencesManager.getInstance().getUserPreferences().getPreferredSourceLanguage());
        targetLanguage.setValue(UserPreferencesManager.getInstance().getUserPreferences().getPreferredTargetLanguage());
        modelType.setValue(ApiClientType.MY_MEMORY);

        textSizeSlider.setValue(UserPreferencesManager.getInstance().getUserPreferences().getPreferredTextSize());
        textSizeLabel.textProperty().bind(Bindings.format("%.0fpx", textSizeSlider.valueProperty()));
        textSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            adjustTextSize(newVal.doubleValue());
        });

        // idk why i can't navigate by a tag #id in javafx when i click on it, so i have to resolve to this.
        webEngine.setOnAlert((WebEvent<String> event) -> {
            String href = event.getData();
            if (href.startsWith("#")) {
                webEngine.executeScript("document.querySelector('" + href + "').scrollIntoView({behavior: 'smooth'});");
            }
        });

        loadStateListener = (obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("app", this);
                webEngine.executeScript("initialize();");
            }
        };
        webEngine.getLoadWorker().stateProperty().addListener(loadStateListener);
    }

    public void loadEpub(String filePath) {
        try {
            Book book = FileManager.getInstance().readEpubFile(filePath);
            String epubCss = FileManager.getInstance().extractCssFromBook(book);
            String customCss = loadCss();
            String htmlContent = EpubToHtmlConverter.convertEpubToHtml(book);
            String completeHtml = buildCompleteHtml(customCss, epubCss, htmlContent);

            webEngine.loadContent(completeHtml);
        } catch (IOException e) {
            if (e instanceof NoSuchFileException) {
                logAndNavigateToHome(e, "File not found!");
            } else {
                logAndNavigateToHome(e, "IO Exception occurred while loading the EPUB file.");
            }
        } catch (Exception e) {
            logAndNavigateToHome(e, "An unexpected error occurred while loading the EPUB file.");
        }
    }

    private void logAndNavigateToHome(Exception e, String userMessage) {
        e.printStackTrace();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(userMessage);
            alert.showAndWait();
            navigateToHome();
        });
    }

    @FXML
    protected void navigateToHome() {
        try {
            clearWebView();
            EpubReaderApp.navigateToHome();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void goToPage() {
        try {
            int pageNumber = Integer.parseInt(pageInput.getText());
            navigateToPage(pageNumber);
        } catch (NumberFormatException e) {
            webEngine.executeScript("showToast(\"Invalid page number!\")");
        }
    }

    private void adjustTextSize(double newSize) {
        String script = """
        var styleElement = document.getElementById('dynamic-font-size');
        if (!styleElement) {
            styleElement = document.createElement('style');
            styleElement.id = 'dynamic-font-size';
            document.head.appendChild(styleElement);
        }
        styleElement.innerHTML = '#epub-body { font-size: %dpx !important; }';
    """.formatted((int) newSize);
        webEngine.executeScript(script);
    }

    @FXML
    protected void toggleTheme() throws IOException {
        isDarkMode = !isDarkMode;
        injectNewCss();
        updateThemeToggleButtonText();
    }

    private void updateThemeToggleButtonText() {
        themeToggleButton.setText(isDarkMode ? "Day Mode" : "Night Mode");
    }

    private void navigateToPage(int pageNumber) {
        String script = "navigateToPage(%d);".formatted(pageNumber);
        webEngine.executeScript(script);
        pageInput.setText("");
    }

    private String loadCss() throws IOException {
        return isDarkMode ?
                FileManager.getInstance().loadCssFromFile("dark-theme.css") :
                FileManager.getInstance().loadCssFromFile("light-theme.css");
    }

    private void injectNewCss() throws IOException {
        String css = loadCss();
        String script = """
        var styleElement = document.getElementById('dynamic-theme-style');
        if (!styleElement) {
            styleElement = document.createElement('style');
            styleElement.id = 'dynamic-theme-style';
            document.head.appendChild(styleElement);
        }
        styleElement.innerHTML = `%s`;
        """.formatted(css.replace("\n", "\\n").replace("\"", "\\\""));
        webEngine.executeScript(script);
    }

    private String buildCompleteHtml(String customCss, String epubCss, String htmlContent) {
        String jsPath = Objects.requireNonNull(getClass().getResource("/script.js")).toExternalForm();

        return "<html><head><style id=\"dynamic-theme-style\">" +
                customCss +
                "</style>" +
                "<style id=\"epub-style\">" +
                epubCss +
                "</style></head><body id=\"epub-body\">" +
                "<div class=\"container\">" +
                htmlContent +
                "</div>" +
                "<div id=\"page-info\"></div>" +
                "<script src=\"" + jsPath + "\"></script></body></html>";
    }

    private void sendTranslationToView(String elementId, String translatedText) {
        try {
            String escapedElementId = elementId.replace("'", "\\'");
            String escapedTranslatedText = translatedText.replace("'", "\\'");

            String script = String.format("handleTranslationResultById('%s', '%s');",
                    escapedElementId, escapedTranslatedText);
            webEngine.executeScript(script);
        } catch (Exception ex) {
            System.err.println("Callback invocation failed: " + ex.getMessage());
        }
    }

    private void sendTranslationErrorToView(String elementId, String message) {
        try {
            String escapedElementId = elementId.replace("'", "\\'");
            String escapedMessage = message.replace("'", "\\'");

            String script = String.format("handleTranslationError('%s', '%s');",
                    escapedElementId, escapedMessage);
            webEngine.executeScript(script);
        } catch (Exception ex) {
            System.err.println("Callback invocation failed: " + ex.getMessage());
        }
    }

    public void getTranslationAsync(String originalText, String elementId) {
        System.out.println("Received Translate Request: " + originalText + " ID: " + elementId);
        TranslationTask task;
        try {
            task = new TranslationTask(
                    originalText,
                    sourceLanguage.getValue(),
                    targetLanguage.getValue(),
                    ApiClientService.getClient(ApiClientType.MICROSOFT_TRANSLATOR)
            );
        } catch (Exception e) {
            Platform.runLater(() -> sendTranslationErrorToView(elementId, e.getMessage()));
            return;
        }

        task.setOnSucceeded(e -> {
            String translatedText = task.getValue();
            Platform.runLater(() -> sendTranslationToView(elementId, translatedText));
        });

        task.setOnFailed(e -> {
            System.err.println("Translation failed: " + e.getSource().getException());
            Platform.runLater(() -> sendTranslationErrorToView(elementId, e.getSource().getException().getMessage()));
        });

        TranslationTaskManager.getExecutorService().submit(task);
    }

    public void clearWebView() {
        if (webView != null) {
            // Remove listeners and clear resources
            if (loadStateListener != null) {
                webEngine.getLoadWorker().stateProperty().removeListener(loadStateListener);
            }
            loadStateListener = null;
            webEngine.setOnAlert(null);
            webEngine = null; // Clear the reference
            webView = null; // Clear the reference
            System.gc(); // Request garbage collection
        }
    }


    private void inspectResources(Collection<Resource> resources){
        for (Resource resource : resources) {
            String href = resource.getHref();
            MediaType mediaType = resource.getMediaType();
            int size = (int) resource.getSize();
            System.out.println("-----------------------------------");
            System.out.println("Resource Href: " + href);
            System.out.println("Media Type: " + mediaType);
            System.out.println("Size: " + size);
            System.out.println("-----------------------------------");
        }
    }
}
