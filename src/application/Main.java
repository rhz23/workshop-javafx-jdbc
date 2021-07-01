package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
            ScrollPane scrollPane = loader.load();
            scrollPane.setFitToHeight(true); // comando para alinhar o scrollpane com a altura da janela
            scrollPane.setFitToWidth(true); //comando para alinhar o scrollpane com a largura da janela
            //Scene mainScene = new Scene(parent);
            primaryStage.setScene(new Scene(scrollPane));
            primaryStage.setTitle("Sample JavaFX application");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
