package bg.tuvarna.sit.wms.entities;

import bg.tuvarna.sit.wms.entities.base.BaseUser;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an administrator user within the system.
 * <p>
 * Administrators might have special permissions and roles to manage various
 * aspects of the system. This entity inherits the common user attributes
 * from the {@link BaseUser} class.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see BaseUser
 */
@Entity
@Table(name = "administrators")
@Getter
@Setter
@NoArgsConstructor
public class Administrator extends BaseUser { }
