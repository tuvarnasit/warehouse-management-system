package bg.tuvarna.sit.wms.enums;

/**
 * Enumeration representing the various roles within the system.
 * <p>
 * Different users within the system can have different roles, which may grant
 * them specific permissions or functionalities. This enumeration provides a
 * clear definition of all the possible roles a user can have.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 */
public enum Role {

  ADMIN,
  OWNER,
  AGENT,
  TENANT
}
