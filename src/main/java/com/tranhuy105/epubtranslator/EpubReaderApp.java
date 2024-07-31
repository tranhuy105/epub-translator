package com.tranhuy105.epubtranslator;

import com.tranhuy105.epubtranslator.controllers.EpubViewerController;
import com.tranhuy105.epubtranslator.controllers.PreferencesController;
import com.tranhuy105.epubtranslator.services.TranslationTaskManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EpubReaderApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        navigateToHome();
    }

    public static void navigateToHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EpubReaderApp.class.getResource("home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void navigateToViewer(String filePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EpubReaderApp.class.getResource("epub-viewer.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        EpubViewerController controller = fxmlLoader.getController();
        controller.loadEpub(filePath);
        primaryStage.setTitle("EPUB Viewer");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void openUserPreferenceForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(EpubReaderApp.class.getResource("preferences-form.fxml"));
        Parent root = loader.load();

        PreferencesController controller = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("User Preferences");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));

        controller.setDialogStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        TranslationTaskManager.getExecutorService().shutdown();
    }
}
