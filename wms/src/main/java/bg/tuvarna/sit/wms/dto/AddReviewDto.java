package bg.tuvarna.sit.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddReviewDto {

  private Integer assessment;
  private String description;
}
