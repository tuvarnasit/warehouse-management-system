package bg.tuvarna.sit.wms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewRating {

  VERY_BAD(1),
  BAD(2),
  GOOD(3),
  VERY_GOOD(4),
  EXCELLENT(5);

  private final int rating;
}
