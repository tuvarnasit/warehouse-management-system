package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.session.UserSession;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

import static bg.tuvarna.sit.wms.util.DialogUtil.showDialog;
import static bg.tuvarna.sit.wms.util.DialogUtil.showConfirmationDialog;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;

/**
 * A controller class, which allows an owner to interact with the data of every warehouse he owns in a table.
 * This class also manages the creation, editing and deletion of warehouses.
 */
public class WarehouseControlPanelController {

  @FXML
  private TableView<WarehouseDTO> warehousesTable;
  @FXML
  private TableColumn<WarehouseDTO, String> nameColumn;
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
  private TableColumn<WarehouseDTO, String> actionColumn;

  private final WarehouseService warehouseService;
  private Owner owner;
  private ObservableList<WarehouseDTO> observableData = FXCollections.observableArrayList();

  public WarehouseControlPanelController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  /**
   * This method retrieves the warehouse data from the database, sets up the tableView and displays the retrieved warehouses.
   */
  @FXML
  public void initialize() {

    owner = getOwnerFromUserSession();

    try {
      List<WarehouseDTO> warehouseDTOs = warehouseService.getAllWarehouseDTOsByOwner(owner);
      observableData.addAll(warehouseDTOs);
    } catch (WarehouseServiceException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to retrieve warehouse data", e.getMessage());
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

  /**
   * Creates the cell factory for the actions column consisting of buttons for editing and deleting a warehouse.
   * Attaches an event handler to the buttons to handle the button click.
   *
   * @return a callback function returning the cell factory
   */
  private Callback<TableColumn<WarehouseDTO, String>, TableCell<WarehouseDTO, String>> createActionsCellFactory() {

    return param -> new TableCell<>() {

      final FontAwesomeIconView editButton = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE_ALT);
      final FontAwesomeIconView deleteButton = new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT);
      final FontAwesomeIconView detailsButton = new FontAwesomeIconView(FontAwesomeIcon.EYE);

      {
        editButton.getStyleClass().addAll("action-button", "edit-button");
        deleteButton.getStyleClass().addAll("action-button", "delete-button");
        detailsButton.getStyleClass().addAll("action-button", "details-button");

        editButton.setOnMouseClicked(event -> {

          WarehouseDTO warehouseDTO = getTableRow().getItem();
          DialogController controller = new WarehouseUpdateDialogController(warehouseDTO, warehouseService);
          try {
            showDialog("/views/warehouseCreation.fxml", "Update warehouse", controller);
          } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Unable to open update dialog", e.getMessage());
          }
          refreshTable();
        });

        deleteButton.setOnMouseClicked(event -> {

          WarehouseDTO warehouseDTO = getTableRow().getItem();
          boolean pressedOK = showConfirmationDialog("Confirm warehouse deletion?", "Do you want to delete this warehouse?");

          if (pressedOK) {
            try {
              warehouseService.deleteWarehouse(warehouseDTO);
            } catch (WarehouseServiceException e) {
              showAlert(Alert.AlertType.ERROR, "Unable to delete warehouse", e.getMessage());
            } finally {
              refreshTable();
            }
          }
        });

        detailsButton.setOnMouseClicked(event -> {

          WarehouseDTO warehouseDTO = getTableRow().getItem();
          DialogController controller = new WarehouseDetailsDialogController(warehouseDTO);
          try {
            showDialog("/views/warehouseCreation.fxml", "Update warehouse", controller);
          } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Unable to open details dialog", e.getMessage());
          }
        });
      }

      @Override
      protected void updateItem(String item, boolean isEmpty) {

        super.updateItem(item, isEmpty);
        HBox hBox = new HBox(8);
        hBox.setAlignment(Pos.CENTER);

        if (isEmpty) {
          setGraphic(null);
          return;
        }
        if(getTableRow().getItem().getStatus() == WarehouseStatus.AVAILABLE)  {
          hBox.getChildren().setAll(editButton, deleteButton);
        } else {
          hBox.getChildren().setAll(detailsButton);
        }
        setGraphic(hBox);
      }
    };
  }

  /**
   * Handles the add button click event.
   * Opens a form for creating a new warehouse as a dialog window.
   */
  @FXML
  public void handleAdd() {

    DialogController controller = new WarehouseCreationDialogController(warehouseService);
    try {
      showDialog("/views/warehouseCreation.fxml", "Add a warehouse", controller);
    } catch (IOException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to open creation dialog", e.getMessage());
    }
    refreshTable();
  }

  /**
   * Refreshes the data in the table view.
   * This method retrieves the owner's warehouses and refreshes the table.
   */
  public void refreshTable() {

    try {
      observableData.setAll(warehouseService.getAllWarehouseDTOsByOwner(owner));
    } catch (WarehouseServiceException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to refresh warehouse data", e.getMessage());
    }
    warehousesTable.refresh();
  }

  private Owner getOwnerFromUserSession() {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if(currentUser.getRole().equals(Role.OWNER)) {
      return (Owner) currentUser;
    }
    throw new IllegalStateException("Only owners are allowed to access this operation");
  }

}
