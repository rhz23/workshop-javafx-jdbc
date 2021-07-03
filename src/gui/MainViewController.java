package gui;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem menuItemDepartment;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private void onMenuItemSellerAction(){
        System.out.println("onMenuItemSellerAction");
    }

    @FXML
    private void onMenuItemDepartmentAction(){
        System.out.println("onMenuItemDepartmentAction");
    }

    @FXML
    private void onMenuItemAboutAction(){
        loadView("/gui/About.fxml");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    private synchronized void loadView(String absoluteName){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVbox = loader.load();

            Scene mainScene = Main.getMainScene();
            VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
            //getroot pega o primeiro elemento da mainScene
            //(ScrollPane) faz o upcasting para ScrollPane (que é o tipo do painel utilizado na MainScene)
            //getContent pega o conteudo dendo do ScrollPane da mainScene
            //(VBox) faz o upcasting do que foi pega dentro do ScrollPane, que já é do tipo Vbox.
            //toda essa informação é guardada dentro da variável "mainVbox"

            Node mainMenu = mainVbox.getChildren().get(0);
            //o comando acima pega o primeiro "children" dentro da mainVBox (filho da pasição 0)
            mainVbox.getChildren().clear();
            //o comando acima apaga todos os filhos que estiverem na mainVbox no momento que é chamado
            mainVbox.getChildren().add(mainMenu);
            //o comando acima adiciona o "mainMenu" ao mainVbox que estava limpo
            mainVbox.getChildren().addAll(newVbox.getChildren());
            //o comando acima adiciona todos os filhos do newVBox
        }
        catch(IOException e){
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
