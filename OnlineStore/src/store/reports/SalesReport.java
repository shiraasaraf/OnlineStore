/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.reports;

import store.engine.StoreEngine;
import store.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sales report implementation.
 *
 * <p>
 * Generates a report listing all orders in the system, including basic sales
 * information such as order ID, customer, creation time, order status, and
 * total amount.
 * </p>
 *
 * <p>
 * This class is a concrete {@link Report} in the Bridge design pattern,
 * where the report content is defined here and the output format is delegated
 * to a {@link ReportWriter}.
 * </p>
 */
public class SalesReport extends Report {

    /**
     * Constructs a sales report with the given output writer.
     *
     * @param writer the report writer used to output the report
     */
    public SalesReport(ReportWriter writer) {
        super(writer);
    }

    /**
     * Returns the title of the sales report.
     *
     * @return the report title
     */
    @Override
    protected String getTitle() {
        return "Sales Report";
    }

    /**
     * Returns the column headers used in the sales report.
     *
     * @return an array of column header names
     */
    @Override
    protected String[] getHeaders() {
        return new String[] { "orderId", "customer", "createdAt", "status", "totalAmount" };
    }

    /**
     * Builds the data rows for the sales report.
     *
     * <p>
     * Each row represents a single order and contains its identifier,
     * customer username, creation timestamp, current status, and total amount.
     * </p>
     *
     * @param engine the store engine providing access to order data
     * @return a list of rows, where each row is an array of string values
     * @throws NullPointerException if {@code engine} is {@code null}
     */
    @Override
    protected List<String[]> buildRows(StoreEngine engine) {
        Objects.requireNonNull(engine, "engine cannot be null");

        List<String[]> rows = new ArrayList<>();
        for (Order o : engine.getAllOrders()) {
            if (o == null) continue;

            rows.add(new String[] {
                    String.valueOf(o.getOrderID()),
                    safe(o.getCustomerUsername()),
                    String.valueOf(o.getCreatedAt()),
                    String.valueOf(o.getStatus()),
                    String.format("%.2f", o.getTotalAmount())
            });
        }
        return rows;
    }

    /**
     * Returns a non-null string value suitable for report output.
     *
     * @param s the input string
     * @return the input string, or an empty string if {@code s} is {@code null}
     */
    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
