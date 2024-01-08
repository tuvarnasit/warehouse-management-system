package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.contracts.DialogController;
import bg.tuvarna.sit.wms.dto.AddReviewDto;
import bg.tuvarna.sit.wms.enums.ReviewRating;
import bg.tuvarna.sit.wms.service.ReviewService;
import bg.tuvarna.sit.wms.session.UserSession;
import static bg.tuvarna.sit.wms.util.ViewLoaderUtil.showAlert;
import bg.tuvarna.sit.wms.validation.ValidatingComboBox;
import bg.tuvarna.sit.wms.validation.ValidatingTextArea;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class DialogAddReviewController implements DialogController {

  @Setter
  @Getter
  private Stage dialogStage;

  @FXML
  private ValidatingComboBox<ReviewRating> assessment;

  @FXML
  private ValidatingTextArea review;

  private final ReviewService reviewService;

  public DialogAddReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @FXML
  public void initialize() {

    assessment.getItems().setAll(ReviewRating.values());
    assessment.setValue(ReviewRating.GOOD);

    assessment.setUp("Assessment condition cannot be empty");
    review.setUp(value -> value.length() >= 10, "Review must be at least 10 characters long.");
  }

  @FXML
  public void onSave() {

    if(!validate()){
      showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid data provided.");
      return;
    }

    AddReviewDto addReviewDto = createAddReviewDto();

    try {
      reviewService.createAndPersistReview(reviewService.getAgentId(),
              UserSession.getInstance().getCurrentUser(), addReviewDto);
      showAlert(Alert.AlertType.INFORMATION, "Success!", "Your review is sent successfully!");
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Review creation was unsuccessful", e.getMessage());
    }

    getDialogStage().close();
  }

  @FXML
  public void onCancel() {

    dialogStage.close();
  }

  private AddReviewDto createAddReviewDto() {

    return new AddReviewDto(assessment.getValue().getRating(),
            review.getText());
  }

  private boolean validate() {

    return review.getIsValid().get() && assessment.getIsValid().get();
  }
}
