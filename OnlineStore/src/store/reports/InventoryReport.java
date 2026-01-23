/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.reports;

import store.engine.StoreEngine;
import store.products.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventory report implementation.
 *
 * <p>
 * Generates a report listing all products currently stored in the system,
 * including their category, price, and available stock.
 * </p>
 *
 * <p>
 * This class represents a concrete {@link Report} in the Bridge design pattern,
 * where the report structure (abstraction) is separated from the output mechanism
 * ({@link ReportWriter}).
 * </p>
 */
public class InventoryReport extends Report {

    /**
     * Constructs an inventory report with the given output writer.
     *
     * @param writer the report writer used to output the report
     */
    public InventoryReport(ReportWriter writer) {
        super(writer);
    }

    /**
     * Returns the title of the inventory report.
     *
     * @return the report title
     */
    @Override
    protected String getTitle() {
        return "Inventory Report";
    }

    /**
     * Returns the column headers used in the inventory report.
     *
     * @return an array of column header names
     */
    @Override
    protected String[] getHeaders() {
        return new String[] { "name", "category", "price", "stock" };
    }

    /**
     * Builds the data rows for the inventory report.
     *
     * <p>
     * Each row represents a single product and contains the product name,
     * category, price, and current stock quantity.
     * </p>
     *
     * @param engine the store engine providing access to product data
     * @return a list of rows, where each row is an array of string values
     * @throws NullPointerException if {@code engine} is {@code null}
     */
    @Override
    protected List<String[]> buildRows(StoreEngine engine) {
        Objects.requireNonNull(engine, "engine cannot be null");

        List<String[]> rows = new ArrayList<>();
        for (Product p : engine.getAllProducts()) {
            if (p == null) continue;

            rows.add(new String[] {
                    safe(p.getName()),
                    safe(String.valueOf(p.getCategory())),
                    String.format("%.2f", p.getPrice()),
                    String.valueOf(p.getStock())
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
