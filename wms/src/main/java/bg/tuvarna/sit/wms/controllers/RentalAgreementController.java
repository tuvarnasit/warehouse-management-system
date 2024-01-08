package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseMenuController;
import bg.tuvarna.sit.wms.dto.WarehouseRentalAgreementDto;
import bg.tuvarna.sit.wms.service.ReviewService;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.session.UserSession;
import bg.tuvarna.sit.wms.util.DialogUtil;
import bg.tuvarna.sit.wms.util.ViewLoaderUtil;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RentalAgreementController extends BaseMenuController {

  private static final Logger LOGGER = LogManager.getLogger(RentalAgreementController.class);

  @FXML
  private TableView<WarehouseRentalAgreementDto> rentalAgreementTable;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, Integer> nameColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> addressColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, Double> sizeColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> statusColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> storageTypeColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> climateConditionColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> startDateConditionColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> endDateConditionColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, BigDecimal> pricePerMonthColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> agentFirstNameColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, String> agentEmailColumn;
  @FXML
  private TableColumn<WarehouseRentalAgreementDto, Void> reviewAgentColumn;


  private final ObservableList<WarehouseRentalAgreementDto> rentalAgreementList = FXCollections.observableArrayList();

  private final WarehouseService warehouseService;
  private final ReviewService reviewService;

  public RentalAgreementController(WarehouseService warehouseService, ReviewService reviewService) {
    this.warehouseService = warehouseService;
    this.reviewService = reviewService;
  }

  @Override
  @FXML
  protected void initialize() {

    super.initialize();

    rentalAgreementTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    rentalAgreementTable.setTableMenuButtonVisible(true);

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getDescription()));
    storageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("storageType"));
    climateConditionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClimateCondition().toString()));
    startDateConditionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(new SimpleDateFormat("dd/MM/yyyy").format(cellData.getValue().getStartDate())));
    endDateConditionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(new SimpleDateFormat("dd/MM/yyyy").format(cellData.getValue().getEndDate())));
    pricePerMonthColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerMonth"));
    agentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("agentFirstName"));
    agentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("agentEmail"));

    reviewAgentColumn.setCellFactory(col -> new TableCell<>() {
      private final Button reviewButton = new Button("Review agent");

      {
        reviewButton.setOnAction(event -> {
          WarehouseRentalAgreementDto dto = getTableView().getItems().get(getIndex());
          reviewAgent(dto);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(reviewButton);
        }
      }
    });

    loadRentalAgreements();

    rentalAgreementTable.setItems(rentalAgreementList);
  }

  private void reviewAgent(WarehouseRentalAgreementDto dto) {

    try {
      reviewService.setAgentId(dto.getAgentId());
      DialogUtil.showDialog("/views/add-review.fxml", "Write a review",
              new DialogAddReviewController(reviewService));
    } catch (IOException e) {
      LOGGER.error(e);
      showAlert(Alert.AlertType.ERROR, "Unable to open creation dialog", e.getMessage());
    }
  }

  private void loadRentalAgreements() {

    try {
      List<WarehouseRentalAgreementDto> rentalAgreements =
              warehouseService.getWarehousesWithRentalAgreementsForOwner(UserSession.getInstance().getCurrentUser().getId());
      rentalAgreementList.clear();
      rentalAgreementList.addAll(rentalAgreements);
      adjustTableHeight(rentalAgreements.size());
    } catch (Exception e) {
      LOGGER.error("Exception occurred while loading reviews: {}", e.getMessage(), e);
    }
  }

  private void adjustTableHeight(int numberOfReviews) {

    double rowHeight = 55;
    double minHeight = 65;
    double height = Math.max(minHeight, numberOfReviews * rowHeight);
    rentalAgreementTable.setStyle("-fx-pref-height: " + height + "px;");
  }
}
