package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {


    private Seller entity;

    private SellerService service;
    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private TextField txtBaseSalary;

    @FXML
    private Label labelErrorName;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    private ObservableList<Department> obsList;

    public void setSeller(Seller entity){
        this.entity = entity;
    }

    public void setServices(SellerService service, DepartmentService departmentService){
        this.service = service;
        this.departmentService = departmentService;
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @FXML
    private void onBtSaveAction(ActionEvent event){
        if (entity == null){
            throw new IllegalStateException("Entity was Null!");
        }
        if (service == null){
            throw new IllegalStateException("Service was Null!");
        }
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataSetChangeListeners();
            Utils.currentStage(event).close(); //comando para fechar a janela, necess??rio colocar o "event" como paramtro de entrada do m??todo
        }
        catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
        catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private void notifyDataSetChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Seller getFormData() {
        Seller obj = new Seller();

        ValidationException exception = new ValidationException("Validation Exception");

        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if (txtName.getText() == null || txtName.getText().trim().equals("")){
            exception.addErrors("name", "Field can??t be empty");
        }
        obj.setName(txtName.getText());

        if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")){
            exception.addErrors("email", "Field can??t be empty");
        }
        obj.setEmail(txtEmail.getText());

        if (dpBirthDate.getValue() == null){
            exception.addErrors("birthDate", "Field can??t be empty");
        }
        else{
            Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
            obj.setBirthDate(Date.from(instant));
        }

        if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")){
            exception.addErrors("baseSalary", "Field can??t be empty");
        }
        obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

        if(exception.getErrors().size() > 0){
            throw exception;
        }

        obj.setDepartment(comboBoxDepartment.getValue());

        return obj;
    }

    @FXML
    private void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();

    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextMaxLength(txtName, 70);
        Constraints.setTextFieldDouble(txtBaseSalary);
        Constraints.setTextMaxLength(txtEmail, 60);
        Utils.formatDatePicker(dpBirthDate,"dd/MM/yyyy");

        initializeComboBoxDepartment();
    }

    public void updateFormData(){
        if (entity == null){
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(entity.getId())); //m??todo para popular o campo Id do formulario com o valor do Id do departamento
        txtName.setText(entity.getName());
        txtEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US); // usado somente para garantir que sera utilizado o ponto e n??o a virgula nos valores
        txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        if (entity.getBirthDate() != null){
            dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); //no banco de dados a data ?? do tipo java.util.date, mas o datePicker ?? do tipo "LocalDate" (pois no bando de dados ?? colocado um hor??rio "universal"(?), por??m para mostrar para o usu??riop ?? interessante mostrar o hor??rio com base no fuso hor??rio dele.
        }

        if (entity.getDepartment() == null){
            comboBoxDepartment.getSelectionModel().selectFirst();
        }
        else {
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    public void loadAssociateObjects(){
        if (departmentService == null){
            throw new IllegalStateException("DepartmentService was null!");
        }

        List<Department> list = departmentService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(obsList);
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields  = errors.keySet();

        /*
        if(fields.contains("name")){
            labelErrorName.setText(errors.get("name"));
        }
        else{
            labelErrorName.setText("");
        }

         */

        labelErrorName.setText((fields.contains("name") ? errors.get("name") : "")); //operador ternario representando a mesma logica acima
        labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
        labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
        labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));

    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }

}
