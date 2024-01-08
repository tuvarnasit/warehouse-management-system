package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseMenuController;
import bg.tuvarna.sit.wms.dto.AgentDTO;
import bg.tuvarna.sit.wms.dto.RequestDetailsDTO;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.exceptions.RequestCreationException;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.RentalRequestService;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.session.UserSession;
import bg.tuvarna.sit.wms.validation.ValidatingComboBox;
import bg.tuvarna.sit.wms.validation.ValidatingTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.util.List;

import static bg.tuvarna.sit.wms.util.ValidationUtils.validateDatePickers;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;

/**
 * Controller class for creating warehouse rental requests.
 * Responsible for initializing and handling user interactions in the warehouse rental view.
 * Uses ValidatingComboBox, ValidatingTextField for input validation and display.
 * Utilizes RentalRequestService and WarehouseService for business logic.
 *
 */
public class WarehouseRentalController extends BaseMenuController {

  @FXML
  private ValidatingComboBox<WarehouseDTO> warehousesComboBox;
  @FXML
  private DatePicker startDatePicker;
  @FXML
  private DatePicker endDatePicker;
  @FXML
  private ValidatingTextField rentPriceField;
  @FXML
  private TextField agentFilterField;
  @FXML
  private ListView<HBox> agentsListView;
  @FXML
  private Label selectedAgentsLabel;

  private final RentalRequestService rentalRequestService;
  private final WarehouseService warehouseService;
  private Owner owner;
  private final ObservableList<WarehouseDTO> availableWarehouses = FXCollections.observableArrayList();
  private final ObservableList<AgentDTO> allAgents = FXCollections.observableArrayList();
  private final ObservableList<AgentDTO> selectedAgents = FXCollections.observableArrayList();

  public WarehouseRentalController(RentalRequestService rentalRequestService, WarehouseService warehouseService) {
    this.rentalRequestService = rentalRequestService;
    this.warehouseService = warehouseService;
  }

  /**
   * Initializes the controller, sets up UI components, and populates them with initial data.
   * Retrieves available warehouses, sets up input validation, and binds properties.
   */
  public void initialize() {

    super.initialize();
    owner = getOwnerFromUserSession();
    try {
      availableWarehouses.setAll(warehouseService.getAvailableWarehouseDTOsByOwner(owner));
    } catch (WarehouseServiceException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to retrieve warehouses", e.getMessage());
    }
    warehousesComboBox.setItems(availableWarehouses);
    warehousesComboBox.setUp("You must select a warehouse");
    rentPriceField.setUp(value -> value.matches("^\\d{1,3}(,?\\d{3})*(\\.\\d{1,2})?$"), "Invalid money format");
    allAgents.setAll(rentalRequestService.getAllAgentDTOs());
    agentsListView.setItems(createRows(allAgents));
    agentFilterField.setOnKeyTyped(e -> filterAgents());

    SimpleStringProperty selectedLabelText = new SimpleStringProperty();
    selectedLabelText.bind(Bindings.concat("Selected Agents: ",Bindings.size(selectedAgents)));
    selectedAgentsLabel.textProperty().bind(selectedLabelText);
  }

  /**
   * Creates an observable list with hboxes, each representing an agentDTO
   * and a way to select the agent using a checkbox.
   * @param agentDTOs the list of agents
   * @return a list of hboxes containing the agent information and a checkbox
   */
  private ObservableList<HBox> createRows(List<AgentDTO> agentDTOs) {
    return FXCollections.observableArrayList(
        agentDTOs.stream().map(this::createSingleRow).toList()
    );
  }

  /**
   * Creates a single row for the agent listview. Adds a checkbox for selecting an agent
   *
   * @param agentDTO the agentDTO fro which to create a row
   * @return hbox containing the agent details and a checkbox
   */
  private HBox createSingleRow(AgentDTO agentDTO) {
    
    CheckBox checkBox = new CheckBox();
    checkBox.setOnAction(event -> handleCheckBoxAction(agentDTO, checkBox.isSelected()));
    checkBox.setSelected(selectedAgents.contains(agentDTO));

    Label label = new Label(agentDTO.toString());
    return new HBox(16, checkBox, label);
  }

  private void handleCheckBoxAction(AgentDTO agentDTO, boolean isSelected) {
    
    if (isSelected) {
      selectedAgents.add(agentDTO);
    } else {
      selectedAgents.remove(agentDTO);
    }
  }

  private void uncheckAllCheckboxes() {

    agentsListView.getItems().forEach(item -> {
      HBox row = item;
      if (row != null && row.getChildren().size() >= 2) {
        Node node = row.getChildren().get(0);
        if (node instanceof CheckBox) {
          CheckBox checkBox = (CheckBox) node;
          checkBox.setSelected(false);
        }
      }
    });
    selectedAgents.clear();
  }

  private void reset() {

    try {
      availableWarehouses.setAll(warehouseService.getAvailableWarehouseDTOsByOwner(owner));
    } catch (WarehouseServiceException e) {
      showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
    }
    rentPriceField.clear();
    warehousesComboBox.setValue(null);
    uncheckAllCheckboxes();
  }

  private void filterAgents() {

    String filterText = agentFilterField.getText().toLowerCase();
    List<HBox> filteredAgents = allAgents.stream()
        .filter(agent -> agent.toString().toLowerCase().contains(filterText))
        .map(this::createSingleRow)
        .toList();

    agentsListView.getItems().setAll(filteredAgents);
  }

  /**
   * Handles the process of sending rental requests to the selected agents.
   * Validates input fields and sends requests via the RentalRequestService.
   * Resets the view after successful request submission.
   */
  public void handleRentRequest() {

    if(!validate()) {
      showAlert(Alert.AlertType.WARNING, "Invalid fields", "Ensure you have correctly filled all fields");
      return;
    }

    try {
      rentalRequestService.createRentalRequests(selectedAgents, createRequestDetailsDTO());
    } catch (RequestCreationException e) {
      showAlert(Alert.AlertType.ERROR, "Unable to send requests", e.getMessage());
    }

    reset();
  }

  /**
   * Validates the input fields for the rental request.
   *
   * @return True if all fields are valid; false otherwise.
   */
  private boolean validate() {
    return !selectedAgents.isEmpty() && warehousesComboBox.getIsValid().get()
        && rentPriceField.getIsValid().get() && validateDatePickers(startDatePicker, endDatePicker);
  }

  private RequestDetailsDTO createRequestDetailsDTO() {

    RequestDetailsDTO dto = new RequestDetailsDTO();

    dto.setWarehouseDTO(warehousesComboBox.getValue());
    dto.setStartDate(startDatePicker.getValue());
    dto.setEndDate(endDatePicker.getValue());
    String monthlyRent = rentPriceField.getText().trim().replaceAll(",","");
    dto.setMonthlyRent(new BigDecimal(monthlyRent));

    return dto;
  }

  private Owner getOwnerFromUserSession() {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if(currentUser.getRole().equals(Role.OWNER)) {
      return (Owner) currentUser;
    }
    throw new IllegalStateException("Only owners are allowed to access this operation");
  }

}
