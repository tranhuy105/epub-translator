package com.tranhuy105.epubtranslator.controllers;

import com.tranhuy105.epubtranslator.EpubReaderApp;
import com.tranhuy105.epubtranslator.models.RecentFile;
import com.tranhuy105.epubtranslator.services.RecentFilesManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

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
            HBox item = getListViewItem(file);
            recentFilesListView.getItems().add(item);
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

    private HBox getListViewItem(RecentFile file) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px; -fx-padding: 10px; -fx-spacing: 10px; -fx-alignment: center-left;");

        ImageView imageView = getImageView(file);
        VBox metadataView = getMetadataVBox(file);

        hBox.getChildren().addAll(imageView, metadataView);
        hBox.setOnMouseClicked(event -> {
            try {
                EpubReaderApp.navigateToViewer(file.getFilePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return hBox;
    }

    private VBox getMetadataVBox(RecentFile file) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setMaxWidth(500);
        vBox.setSpacing(5);

        Label titleLabel = new Label(file.getTitle());
        titleLabel.setStyle("-fx-font-size: 20px;-fx-font-weight: bold; -fx-text-fill: #333333; -fx-wrap-text: true");

        Label authorLabel = new Label(file.getAuthor());
        authorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-wrap-text: true");

        Label pathLabel = new Label(file.getFilePath());
        pathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-wrap-text: true");


        vBox.getChildren().addAll(titleLabel,authorLabel,pathLabel);
        return vBox;
    }

    private ImageView getImageView(RecentFile file) {
        if (file.getCoverImage() == null || file.getCoverImage().isEmpty()) {
           return new ImageView();
        }
        ImageView imageView = new ImageView(new Image(file.getCoverImage()));
        imageView.setFitHeight(150);
        imageView.setFitWidth(100);
        imageView.setStyle("-fx-padding: 5px;");
        return imageView;
    }
}
