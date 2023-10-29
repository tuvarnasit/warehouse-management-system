package bg.tuvarna.sit.wms.entities.base;

import bg.tuvarna.sit.wms.enums.NotificationStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a base notification with common attributes to serve as a foundation
 * for more specific notification entities.
 * <p>
 * This class is marked as a mapped superclass, meaning it won't be persisted
 * as an entity itself, but will provide common attributes to its subclasses.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseNotification extends BaseEntity {

  @Column(name = "message", length = 128, nullable = false)
  private String message;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private NotificationStatus status;

  @PrePersist
  protected void onCreate() {
    date = LocalDateTime.now();
  }
}
