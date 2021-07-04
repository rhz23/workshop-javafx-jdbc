package gui;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

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
    public void onBtNewAction(){
        System.out.println("onBtNewAction");
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
}
