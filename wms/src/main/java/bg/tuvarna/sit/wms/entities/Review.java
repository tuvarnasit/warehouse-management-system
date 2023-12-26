package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends BaseEntity {

  @Column(name = "assessment", nullable = false)
  private Integer assessment;

  @Column(name = "description", length = 500)
  private String description;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false, referencedColumnName = "id")
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver_id", nullable = false, referencedColumnName = "id")
  private User receiver;

  // TODO: Extract validation logic to validator layer
  public void setAssessment(Integer assessment) {
    if (assessment < 1 || assessment > 5) {
      throw new IllegalArgumentException("Assessment must be between 1 and 5.");
    }
    this.assessment = assessment;
  }
}
