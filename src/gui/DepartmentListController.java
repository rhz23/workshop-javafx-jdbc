package gui;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    //Dependencia da classe DepartmentService (criada a variavel service do tipo DepartmentService)
    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private TableColumn<Department, Department> tableColumnEDIT;

    @FXML
    private TableColumn<Department, Department> tableColumnREMOVE;

    @FXML
    private Button btNew;

    private ObservableList<Department> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Department object = new Department();
        createDialogForm(object, "/gui/DepartmentForm.fxml", parentStage);
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

    //m??todo responsavel por atualizar a ObservableList obsList
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
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Department obj, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); //carrega a view passada como parametro no absolutename
            Pane pane = loader.load(); //carrega a view

            //inje????o do "Department obj" no DepartmentFormController
            DepartmentFormController controller = loader.getController(); //pegar referencia para o controlador
            controller.setDepartment(obj); //setar o Department com o obj recebido
            controller.setDepartmentService(new DepartmentService());
            controller.subscribeDataChangeListener(this); //se inscreve para escutar os eventos de mudan??a
            controller.updateFormData(); //atualizar os valores do FormData com os valores do obj acima

            Stage dialogStage = new Stage(); //como sera criada uma janela na frente de outra, ?? necess??rio criar um novo "palco" (stage) de forma que ?? necess??rio instanciar um novo stage para comportar a nova janela
            dialogStage.setTitle("Department data"); // titulo da janela
            dialogStage.setScene(new Scene(pane)); //como ?? um novo stage, ?? necess??rio criar uma nova scene que ser?? apresentado nele, neste caso a scene ?? aquela que foi carregada anteriormente
            dialogStage.setResizable(false); //o setResizable ele diz se a janela pode ou n??o ser redimensionada, colocando "false" ela n??o poder?? ser redimensionada
            dialogStage.initOwner(parentStage); //indica quem ?? o "pai" da nova janela a ser aberta, no caso ?? a janela que foi passada como parametro no m??todo (parentStage)
            dialogStage.initModality(Modality.WINDOW_MODAL); //define que a jenala ?? "modal", que ela fica "travada" (enquanto n??o fechar ela, n??o ?? poss??vel acessar a janela anterior)
            dialogStage.showAndWait(); //apresenta a nova janela e espera
        }
        catch (IOException e){
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error Loading View", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView(); //quando h?? um evento de mudan??a, chama o m??todo de atualiza????o da vis??o da tabela
    }

    private void initEditButtons(){ //cria um bot??o de edit em cada linha da tabela, que quando clicado abre um formulario de edi????o
        tableColumnEDIT.setCellValueFactory(param ->new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>(){
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null){
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
            }
        });
    }
    
    private void initRemoveButtons(){
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>(){
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Department obj, boolean empty){
                super.updateItem(obj, empty);
                if (obj == null){
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Department obj) {
        Optional<ButtonType> result =  Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

        if (result.get() == ButtonType.OK){
            if (service == null){
                throw new IllegalStateException("Service was null");
            }
            try {
                service.remove(obj);
                updateTableView();
            }
            catch (DbIntegrityException e){
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

}
