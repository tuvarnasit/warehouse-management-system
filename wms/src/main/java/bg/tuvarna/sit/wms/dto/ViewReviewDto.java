package bg.tuvarna.sit.wms.dto;

import bg.tuvarna.sit.wms.entities.Review;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ViewReviewDto {

  private Integer assessment;
  private String description;
  private String senderName;

  public ViewReviewDto(Review review) {
    this.assessment = review.getAssessment();
    this.description = review.getDescription();
    this.senderName = review.getSender().getFirstName() + " " + review.getSender().getLastName();
  }

}
