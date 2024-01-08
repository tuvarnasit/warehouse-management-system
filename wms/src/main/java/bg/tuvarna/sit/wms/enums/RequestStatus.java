package bg.tuvarna.sit.wms.enums;

/**
 * Enum representing the status of a rental request. A rental request can be in one of the following states:
 * <ul>
 *   <li>{@code PENDING}: Indicates that the rental request is pending and awaiting to be accepted or declined</li>
 *
 *   <li>{@code ACCEPTED}: Indicates that the rental request has been accepted</li>
 *
 *   <li>{@code DECLINED}: Indicates that the rental request has been declined</li>
 * </ul>
 */
public enum RequestStatus {

  PENDING,
  ACCEPTED,
  DECLINED
}
