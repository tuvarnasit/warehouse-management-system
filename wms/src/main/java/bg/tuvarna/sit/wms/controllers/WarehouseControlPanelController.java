package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.util.DialogUtil;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

public class WarehouseControlPanelController {

  @FXML
  public TableColumn<WarehouseDTO, String> nameColumn;
  @FXML
  private TableView<WarehouseDTO> warehousesTable;
  @FXML
  private TableColumn<WarehouseDTO, String> streetColumn;
  @FXML
  private TableColumn<WarehouseDTO, String> cityColumn;
  @FXML
  private TableColumn<WarehouseDTO, String> countryColumn;
  @FXML
  private TableColumn<WarehouseDTO, Number> sizeColumn;
  @FXML
  private TableColumn<WarehouseDTO, String> statusColumn;
  @FXML
  public TableColumn<WarehouseDTO,String> actionColumn;

  private WarehouseService warehouseService = new WarehouseService();
  private Owner owner;
  private ObservableList<WarehouseDTO> observableData = FXCollections.observableArrayList();

  {
    EntityManager em = JpaUtil.getEntityManager();
    em.getTransaction().begin();

    owner = em.find(Owner.class, 1L);
    em.close();
  }

  @FXML
  public void initialize() {

    try {
      List<WarehouseDTO> warehouseDTOs = warehouseService.getWarehouseDTOsByOwner(owner);
      observableData.addAll(warehouseDTOs);
    } catch (WarehouseServiceException e) {
      DialogUtil.showErrorAlert("Unable to retrieve warehouse data", e.getMessage());
    }

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
    cityColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
    countryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
    sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getDescription()));

    actionColumn.setCellFactory(createActionsCellFactory());

    warehousesTable.setItems(observableData);
  }

  private Callback<TableColumn<WarehouseDTO, String>, TableCell<WarehouseDTO, String>> createActionsCellFactory() {

    return param -> new TableCell<>() {

      final Button editButton = new Button("Edit");
      final Button deleteButton = new Button("Delete");

      {
        editButton.setOnAction(event -> {

          WarehouseDTO warehouseDTO = getTableView().getItems().get(getIndex());
          DialogController controller = new WarehouseUpdateDialogController(owner, warehouseDTO);
          try {
            DialogUtil.showDialog("/views/warehouseCreation.fxml", "Update warehouse", controller);
          } catch (IOException e) {
            DialogUtil.showErrorAlert("Unable to open update dialog", e.getMessage());
          }
          refreshTable();
        });

        deleteButton.setOnAction(event -> {

          WarehouseDTO warehouseDTO = getTableView().getItems().get(getIndex());

          Alert confirmDelete = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);
          confirmDelete.setTitle("Confirm warehouse deletion?");
          confirmDelete.setHeaderText("Do you want to delete this warehouse?");
          ButtonType buttonPressed = confirmDelete.showAndWait().get();

          if (buttonPressed.equals(ButtonType.YES)) {
            try {
              warehouseService.deleteWarehouse(warehouseDTO);
            } catch (WarehouseServiceException e) {
              DialogUtil.showErrorAlert("Unable to delete warehouse", e.getMessage());
            }
            refreshTable();
          }
        });
      }

      @Override
      protected void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(new HBox(8, editButton, deleteButton));
        }
      }
    };
  }

  @FXML
  public void handleAdd() {

    DialogController controller = new WarehouseCreationDialogController(owner);
    try {
      DialogUtil.showDialog("/views/warehouseCreation.fxml", "Add a warehouse", controller);
    } catch (IOException e) {
      DialogUtil.showErrorAlert("Unable to open creation dialog", e.getMessage());
    }
    refreshTable();
  }

  public void refreshTable() {

    try {
      observableData.setAll(warehouseService.getWarehouseDTOsByOwner(owner));
    } catch (WarehouseServiceException e) {
      DialogUtil.showErrorAlert("Unable to refresh warehouse data", e.getMessage());
    }
    warehousesTable.refresh();
  }

}
