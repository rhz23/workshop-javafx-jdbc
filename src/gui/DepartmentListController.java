package gui;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable {

    //Dependencia da classe DepartmentService (criada a variavel service do tipo DepartmentService)
    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private Button btNew;

    private ObservableList<Department> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        createDialogForm("/gui/DepartmentForm.fxml", parentStage);
    }

    //seta a variavel service com o DepartmentService que foi criado em outro lugar
    public void setDepartmentService(DepartmentService service){
        this.service = service;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    //iniciar comportamento das tabelas
    private void initializeNodes() {
        //configura a tabela id
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        //configura a tabela name
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    //método responsavel por atualizar a ObservableList obsList
    public void updateTableView(){
        //verificar se o service foi carregado corretamente
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        List<Department> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        //o comando acima instancia a obsList pegando os dados da list acima
        //carrega os elementos da obsList na tableViewDepartment
        tableViewDepartment.setItems(obsList);
    }

    private void createDialogForm(String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); //carrega a view passada como parametro no absolutename
            Pane pane = loader.load(); //carrega a view

            Stage dialogStage = new Stage(); //como sera criada uma janela na frente de outra, é necessário criar um novo "palco" (stage) de forma que é necessário instanciar um novo stage para comportar a nova janela
            dialogStage.setTitle("Department data"); // titulo da janela
            dialogStage.setScene(new Scene(pane)); //como é um novo stage, é necessário criar uma nova scene que será apresentado nele, neste caso a scene é aquela que foi carregada anteriormente
            dialogStage.setResizable(false); //o setResizable ele diz se a janela pode ou não ser redimensionada, colocando "false" ela não poderá ser redimensionada
            dialogStage.initOwner(parentStage); //indica quem é o "pai" da nova janela a ser aberta, no caso é a janela que foi passada como parametro no método (parentStage)
            dialogStage.initModality(Modality.WINDOW_MODAL); //define que a jenala é "modal", que ela fica "travada" (enquanto não fechar ela, não é possível acessar a janela anterior)
            dialogStage.showAndWait(); //apresenta a nova janela e espera
        }
        catch (IOException e){
            Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
