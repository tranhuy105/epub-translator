package com.tranhuy105.epubtranslator.controllers;

import com.tranhuy105.epubtranslator.EpubReaderApp;
import com.tranhuy105.epubtranslator.models.RecentFile;
import com.tranhuy105.epubtranslator.services.RecentFilesManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML
    private ListView<HBox> recentFilesListView;

    public void initialize() {
        loadRecentFiles();
    }

    private void loadRecentFiles() {
        List<RecentFile> recentFiles = RecentFilesManager.getInstance().getRecentFiles();
        for (RecentFile file : recentFiles) {
            HBox hBox = new HBox();
            hBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px; -fx-padding: 10px; -fx-spacing: 10px; -fx-alignment: center-left;");

            ImageView imageView = new ImageView(new Image(file.getCoverImage()));
            imageView.setFitHeight(150);
            imageView.setFitWidth(100);
            imageView.setStyle("-fx-padding: 5px;");

            Label label = new Label(file.getTitle() + " by " + file.getAuthor());
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-padding: 5px;");

            hBox.getChildren().addAll(imageView, label);
            hBox.setOnMouseClicked(event -> {
                try {
                    EpubReaderApp.navigateToViewer(file.getFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recentFilesListView.getItems().add(hBox);
        }
    }

    @FXML
    protected void loadNewEpub() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EPUB Files", "*.epub"));
        File selectedFile = fileChooser.showOpenDialog(recentFilesListView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                EpubReaderApp.navigateToViewer(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
