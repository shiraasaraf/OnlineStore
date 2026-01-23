package store.reports;

import store.engine.StoreEngine;
import store.products.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventory report: lists all products and their stock details.
 */
public class InventoryReport extends Report {

    public InventoryReport(ReportWriter writer) {
        super(writer);
    }

    @Override
    protected String getTitle() {
        return "Inventory Report";
    }

    @Override
    protected String[] getHeaders() {
        return new String[] { "name", "category", "price", "stock" };
    }

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

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
