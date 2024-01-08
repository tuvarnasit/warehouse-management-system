package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.controllers.base.BaseMenuController;
import bg.tuvarna.sit.wms.dto.ViewReviewDto;
import bg.tuvarna.sit.wms.service.ReviewService;
import bg.tuvarna.sit.wms.session.UserSession;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyReviewsController extends BaseMenuController {

  private static final Logger LOGGER = LogManager.getLogger(MyReviewsController.class);

  @FXML
  private TableView<ViewReviewDto> reviewsTable;

  @FXML
  private TableColumn<ViewReviewDto, Integer> assessmentColumn;

  @FXML
  private TableColumn<ViewReviewDto, String> descriptionColumn;

  @FXML
  private TableColumn<ViewReviewDto, String> senderColumn;
  private final ObservableList<ViewReviewDto> reviewList = FXCollections.observableArrayList();


  private final ReviewService reviewService;
  private final ScheduledExecutorService scheduledExecutorService;


  public MyReviewsController(ReviewService reviewService, ScheduledExecutorService scheduledExecutorService) {
    this.reviewService = reviewService;
    this.scheduledExecutorService = scheduledExecutorService;
  }

  @Override
  @FXML
  protected void initialize() {

    super.initialize();

    reviewsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    assessmentColumn.setCellValueFactory(new PropertyValueFactory<>("assessment"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    senderColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
    reviewsTable.setItems(reviewList);

    startPeriodicRefresh();
  }

  private void loadReviews() {

    try {
      List<ViewReviewDto> reviews =
              reviewService.getReviewsForCurrentUser(UserSession.getInstance().getCurrentUser().getId());
      reviewList.clear();
      reviewList.addAll(reviews);
      adjustTableHeight(reviews.size());
    } catch (Exception e) {
      LOGGER.error("Exception occurred while loading reviews: {}", e.getMessage(), e);
    }
  }

  private void adjustTableHeight(int numberOfReviews) {

    double rowHeight = 43;
    double minHeight = 56;
    double height = Math.max(minHeight, numberOfReviews * rowHeight);
    reviewsTable.setStyle("-fx-pref-height: " + height + "px;");
  }


  private void startPeriodicRefresh() {

    scheduledExecutorService.scheduleAtFixedRate(() ->
            Platform.runLater(this::loadReviews), 0, 10, TimeUnit.SECONDS);
  }
}
