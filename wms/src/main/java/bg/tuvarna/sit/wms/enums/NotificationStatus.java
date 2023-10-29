package bg.tuvarna.sit.wms.enums;

/**
 * Enumeration representing the possible statuses of a notification.
 * <p>
 * Notifications can either be in a 'READ' status, indicating the user has seen
 * the notification, or 'UNREAD', indicating the user has not yet viewed the message.
 * This helps in ensuring users are aware of new alerts or messages and can act
 * accordingly based on their notification status.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 */
public enum NotificationStatus {

    READ,
    UNREAD
}
