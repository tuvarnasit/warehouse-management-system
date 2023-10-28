package bg.tuvarna.sit.wms.entities.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the foundational attributes of a review, serving as a base
 * for specific review entities.
 * <p>
 * This class is marked as a mapped superclass, meaning it won't be persisted
 * as an entity itself, but its attributes will be inherited by its subclasses
 * which represent concrete review entities.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseReview extends BaseEntity {

  @Column(name = "assessment", nullable = false)
  private Integer assessment;

  @Column(name = "description", length = 500)
  private String description;

  public void setAssessment(Integer assessment) {
    if (assessment < 1 || assessment > 5) {
      throw new IllegalArgumentException("Assessment must be between 1 and 5.");
    }
    this.assessment = assessment;
  }
}
