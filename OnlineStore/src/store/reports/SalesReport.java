package store.reports;

import store.engine.StoreEngine;
import store.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sales report: lists all orders with basic sales fields.
 */
public class SalesReport extends Report {

    public SalesReport(ReportWriter writer) {
        super(writer);
    }

    @Override
    protected String getTitle() {
        return "Sales Report";
    }

    @Override
    protected String[] getHeaders() {
        return new String[] { "orderId", "customer", "createdAt", "status", "totalAmount" };
    }

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

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
