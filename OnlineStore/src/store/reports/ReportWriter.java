package store.reports;

import java.io.IOException;
import java.util.List;

/**
 * Defines the output mechanism for reports.
 * Implementations decide where/how the report is written (console, file, etc.).
 */
public interface ReportWriter {

    /**
     * Writes the report.
     *
     * @param reportTitle report title (e.g., "Inventory Report")
     * @param headers     CSV headers (non-null)
     * @param rows        report rows (non-null). Each row length should match headers length.
     * @throws IOException if writing fails
     */
    void write(String reportTitle, String[] headers, List<String[]> rows) throws IOException;
}
