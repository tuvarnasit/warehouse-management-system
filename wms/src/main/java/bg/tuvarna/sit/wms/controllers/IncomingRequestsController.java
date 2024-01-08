package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.context.ApplicationContext;
import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.RentalRequestDTO;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.RequestStatus;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.exceptions.RequestCreationException;
import bg.tuvarna.sit.wms.service.RentalAgreementService;
import bg.tuvarna.sit.wms.service.RentalRequestService;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;

import static bg.tuvarna.sit.wms.util.DialogUtil.showConfirmationDialog;
import static bg.tuvarna.sit.wms.util.DialogUtil.showDialog;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;

/**
 * A controller class which manages the view for incoming rental requests.
 * Displays a table with details of pending requests for the logged-in agent.
 * Allows the agent to view request details and accept or decline the request.
 */
public class IncomingRequestsController {

  @FXML
  private TableView<RentalRequestDTO> requestsTable;
  @FXML
  private TableColumn<RentalRequestDTO, String> warehouseNameColumn;
  @FXML
  private TableColumn<RentalRequestDTO, String> ownerNamesColumn;
  @FXML
  private TableColumn<RentalRequestDTO, String> rentPriceColumn;
  @FXML
  private TableColumn<RentalRequestDTO, String> startDateColumn;
  @FXML
  private TableColumn<RentalRequestDTO, String> endDateColumn;
  @FXML
  private TableColumn<RentalRequestDTO, String> detailsColumn;
  @FXML
  private TableColumn<RentalRequestDTO, String> actionColumn;
  @FXML
  private ToggleButton newRequestsButton;
  @FXML
  private ToggleButton acceptedRequestsButton;
  private final ToggleGroup toggleGroup;

  private final RentalRequestService rentalRequestService;
  private Agent agent;
  private final ObservableList<RentalRequestDTO> rentalRequestDTOS = FXCollections.observableArrayList();

  public IncomingRequestsController(RentalRequestService rentalRequestService) {
    this.rentalRequestService = rentalRequestService;
    toggleGroup = new ToggleGroup();
  }


  /**
   * Initializes the controller. Retrieves the logged-in agent, configures
   * table columns with appropriate cell factories and populates the request table,
   */
  public void initialize() {

    newRequestsButton.setToggleGroup(toggleGroup);
    acceptedRequestsButton.setToggleGroup(toggleGroup);

    agent = getAgentFromUserSession();
    newRequestsButton.setSelected(true);
    rentalRequestDTOS.setAll(rentalRequestService.getIncomingRentalRequestsByAgent(agent));

    warehouseNameColumn.setCellValueFactory(request -> new SimpleStringProperty(request.getValue().getWarehouseDTO().getName()));
    ownerNamesColumn.setCellValueFactory(request -> new SimpleStringProperty(getOwnerFullName(request.getValue().getWarehouseDTO().getOwner())));
    rentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyRent"));
    startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
    endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    detailsColumn.setCellFactory(createDetailsCellFactory());
    actionColumn.setCellFactory(createActionsCellFactory());

    requestsTable.setItems(rentalRequestDTOS);
  }

  /**
   * Creates a cell factory for the "Details" column of the requests table.
   * Displays a button, which when clicked shows a dialog containing
   * detailed information about the warehouse.
   *
   * @return The cell factory for the "Details" column.
   */
  private Callback<TableColumn<RentalRequestDTO, String>, TableCell<RentalRequestDTO, String>> createDetailsCellFactory() {

    return param -> new TableCell<>() {

      final FontAwesomeIconView detailsButton = new FontAwesomeIconView(FontAwesomeIcon.EYE);

      {
        detailsButton.getStyleClass().addAll("action-button", "details-button");

        detailsButton.setOnMouseClicked(event -> {

          WarehouseDTO warehouseDTO = getTableRow().getItem().getWarehouseDTO();
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

        if (isEmpty) {
          setGraphic(null);
        } else {
          hBox.getChildren().setAll(detailsButton);
          hBox.setAlignment(Pos.CENTER);
          setGraphic(hBox);
        }
      }
    };
  }

  /**
   * Creates a cell factory for the "Actions" column in the requests table.
   * Provides buttons to accept or decline incoming rental requests,
   * or a button for creating a rental agreement for already accepted requests.
   *
   * @return The cell factory for the "Actions" column.
   */
  private Callback<TableColumn<RentalRequestDTO, String>, TableCell<RentalRequestDTO, String>> createActionsCellFactory() {

    return param -> new TableCell<>() {

      final FontAwesomeIconView acceptButton = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
      final FontAwesomeIconView declineButton = new FontAwesomeIconView(FontAwesomeIcon.REMOVE);
      final FontAwesomeIconView rentButton = new FontAwesomeIconView(FontAwesomeIcon.CLIPBOARD);

      {
        acceptButton.getStyleClass().addAll("action-button", "accept-button");
        declineButton.getStyleClass().addAll("action-button", "decline-button");
        rentButton.getStyleClass().addAll("action-button", "rent-button");

        acceptButton.setOnMouseClicked(event -> {
          RentalRequestDTO requestDTO = getTableRow().getItem();
          try {
            rentalRequestService.acceptRentalRequest(requestDTO);
          } catch (RequestCreationException e) {
            showAlert(Alert.AlertType.ERROR, "Unable to accept request", e.getMessage());
          } finally {
            refreshTable();
          }
        });

        declineButton.setOnMouseClicked(event -> {

          RentalRequestDTO requestDTO = getTableRow().getItem();
          boolean pressedOK = showConfirmationDialog("Decline request", "Do you want to decline this request?");

          if(pressedOK) {
            try {
              rentalRequestService.declineRentalRequest(requestDTO);
            } catch (RequestCreationException e) {
              showAlert(Alert.AlertType.ERROR, "Unable to decline the request", e.getMessage());
            } finally {
              refreshTable();
            }
          }
        });

        rentButton.setOnMouseClicked(event ->{

          RentalRequestDTO rentalRequestDTO = getTableRow().getItem();
          RentalAgreementService service = new RentalAgreementService(
              ApplicationContext.getWAREHOUSE_SERVICE(),
              rentalRequestService,
              ApplicationContext.getRENTAL_AGREEMENT_DAO());
          DialogController controller = new RentalAgreementCreationController(rentalRequestDTO, service);
          try {
            showDialog("/views/rentalAgreementCreation.fxml", "Rent a warehouse", controller);
          } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Unable to open rent dialog", e.getMessage());
          }
          refreshTable();
        });
      }

      @Override
      protected void updateItem(String item, boolean isEmpty) {

        super.updateItem(item, isEmpty);
        if (isEmpty) {
          setGraphic(null);
          return;
        }

        HBox hBox = new HBox(8);
        hBox.setAlignment(Pos.CENTER);

        RequestStatus status = getTableRow().getItem().getStatus();
        if (status.equals(RequestStatus.PENDING)) {
          hBox.getChildren().setAll(acceptButton, declineButton);
        } else if (status.equals(RequestStatus.ACCEPTED)) {
          hBox.getChildren().setAll(rentButton);
        }

        setGraphic(hBox);
      }
    };
  }

  /**
   * Retrieves all new requests (who have a status of "PENDING") and sets them to the observable list,
   * so they can be displayed in the tableview.
   */
  public void displayNewRequests() {

    rentalRequestDTOS.setAll(rentalRequestService.getIncomingRentalRequestsByAgent(agent));
    requestsTable.refresh();
  }

  /**
   * Retrieves all accepted requests (who have a status of "Accepted") and sets them to the observable list,
   * so they can be displayed in the tableview.
   */
  public void displayAcceptedRequests() {

    rentalRequestDTOS.setAll(rentalRequestService.getAcceptedRentalRequestsByAgent(agent));
    requestsTable.refresh();
  }

  /**
   * Refreshes the table view by getting the newest requests from the database,
   * saving them in the observable list and refreshing the tableview.
   */
  private void refreshTable() {

    if(newRequestsButton.isSelected()) {
      displayNewRequests();
    } else if(acceptedRequestsButton.isSelected()) {
      displayAcceptedRequests();
    }
  }


  private String getOwnerFullName(Owner owner) {
    return owner.getFirstName() + " " + owner.getLastName();
  }

  private Agent getAgentFromUserSession() {

    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser.getRole().equals(Role.AGENT)) {
      return (Agent) currentUser;
    }
    throw new IllegalStateException("Only agents are allowed to access this operation");
  }
}
