/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.shipping;

/**
 * Simulated external shipping service API: FastShip.
 *
 * <p>
 * This represents a 3rd-party API that does NOT match our system interface.
 * It exposes {@link #executeDelivery(int, String, double)} instead of shipOrder(Order).
 * </p>
 */
public class FastShipAPI {

    /**
     * Executes delivery through the external shipping service.
     *
     * @param orderId    order id
     * @param recipient  recipient identifier (e.g., username)
     * @param amount     total order amount
     * @return tracking code (simulated)
     */
    public String executeDelivery(int orderId, String recipient, double amount) {
        if (recipient == null) {
            recipient = "UNKNOWN";
        }
        // simulate tracking code
        return "FS-" + orderId + "-" + Math.abs(recipient.hashCode());
    }
}
