package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

    public static Stage currentStage(ActionEvent event){
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    // tenta converter para número inteiro, e em caso de invalido, retorna null
    public static Integer tryParseToInt(String str){
        try {
            return Integer.parseInt(str);

        }
        catch(NumberFormatException e){
            return null;
        }
    }

}
