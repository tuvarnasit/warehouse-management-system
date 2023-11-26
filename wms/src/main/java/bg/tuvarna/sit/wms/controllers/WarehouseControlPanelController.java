package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.util.DialogUtil;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import javax.persistence.EntityManager;
import java.util.Set;

public class WarehouseControlPanelController {

  @FXML
  private TableView<Warehouse> warehousesTable;
  @FXML
  private TableColumn<Warehouse, String> addressColumn;
  @FXML
  private TableColumn<Warehouse, String> cityColumn;
  @FXML
  private TableColumn<Warehouse, String> countryColumn;
  @FXML
  private TableColumn<Warehouse, Number> sizeColumn;
  @FXML
  private TableColumn<Warehouse, String> statusColumn;
  @FXML
  public TableColumn<Warehouse,String> actionColumn;

  private WarehouseService warehouseService = new WarehouseService();
  private Owner owner;

  {
    EntityManager em = JpaUtil.getEntityManager();
    em.getTransaction().begin();

    owner = em.find(Owner.class, 1L);
        em.close();
  }

  @FXML
  public void initialize() {

    Set<Warehouse> data = owner.getWarehouses();
    ObservableList<Warehouse> observableData = FXCollections.observableArrayList(data);

    addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getStreet()));
    cityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getCity().getName()));
    countryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().getCity().getCountry().getName()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSize()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getDescription()));

    actionColumn.setCellFactory(createActionsCellFactory());

    warehousesTable.getItems().addAll(observableData);
  }
  private Callback<TableColumn<Warehouse, String>, TableCell<Warehouse, String>> createActionsCellFactory() {

    return param -> new TableCell<>() {

      final Button editButton = new Button("Edit");
      final Button deleteButton = new Button("Delete");

      {
        editButton.setOnAction(event -> {
          Warehouse warehouse = getTableView().getItems().get(getIndex());
          // TODO: Add editing functionality
        });

        deleteButton.setOnAction(event -> {
          Warehouse warehouse = getTableView().getItems().get(getIndex());
          // TODO: Add logic for deleting
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
  public void handleAdd(ActionEvent actionEvent) {

    DialogController controller = new WarehouseCreationDialogController(owner);
    DialogUtil.showDialog("/views/warehouseCreation.fxml", "Add a warehouse", controller);
  }
}
