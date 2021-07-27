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
import model.entities.Seller;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    //Dependencia da classe SellerService (criada a variavel service do tipo SellerService)
    private SellerService service;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller, Seller> tableColumnEDIT;

    @FXML
    private TableColumn<Seller, Seller> tableColumnREMOVE;

    @FXML
    private Button btNew;

    private ObservableList<Seller> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Seller object = new Seller();
        createDialogForm(object, "/gui/SellerForm.fxml", parentStage);
    }

    //seta a variavel service com o SellerService que foi criado em outro lugar
    public void setSellerService(SellerService service){
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
        //configura a tabela name
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        //configura a tabela name
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        //formata a data
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        //configura a tabela name
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    //método responsavel por atualizar a ObservableList obsList
    public void updateTableView(){
        //verificar se o service foi carregado corretamente
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        //o comando acima instancia a obsList pegando os dados da list acima
        //carrega os elementos da obsList na tableViewSeller
        tableViewSeller.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }


    private void createDialogForm(Seller obj, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); //carrega a view passada como parametro no absolutename
            Pane pane = loader.load(); //carrega a view

            //injeção do "Seller obj" no SellerFormController
            SellerFormController controller = loader.getController(); //pegar referencia para o controlador
            controller.setSeller(obj); //setar o Seller com o obj recebido
            controller.setSellerService(new SellerService());
            controller.subscribeDataChangeListener(this); //se inscreve para escutar os eventos de mudança
            controller.updateFormData(); //atualizar os valores do FormData com os valores do obj acima

            Stage dialogStage = new Stage(); //como sera criada uma janela na frente de outra, é necessário criar um novo "palco" (stage) de forma que é necessário instanciar um novo stage para comportar a nova janela
            dialogStage.setTitle("Seller data"); // titulo da janela
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



    @Override
    public void onDataChanged() {
        updateTableView(); //quando há um evento de mudança, chama o método de atualização da visão da tabela
    }

    private void initEditButtons(){ //cria um botão de edit em cada linha da tabela, que quando clicado abre um formulario de edição
        tableColumnEDIT.setCellValueFactory(param ->new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>(){
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null){
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons(){
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>(){
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Seller obj, boolean empty){
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

    private void removeEntity(Seller obj) {
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
