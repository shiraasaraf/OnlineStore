/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.reports;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Console-based implementation of {@link ReportWriter}.
 *
 * <p>
 * Writes reports to standard output (stdout) using a simple, human-readable
 * tabular format. Intended mainly for debugging, testing, or quick inspection
 * of report data.
 * </p>
 */
public class ConsoleWriter implements ReportWriter {

    /**
     * Writes a report to the console.
     *
     * <p>
     * The report is printed with a title header, followed by a single header row
     * and zero or more data rows. Each row is printed on a separate line, with
     * columns separated by a vertical bar ({@code |}).
     * </p>
     *
     * @param reportTitle title of the report (must not be {@code null})
     * @param headers     column headers (must not be {@code null})
     * @param rows        report data rows (must not be {@code null})
     * @throws IOException never thrown in this implementation, but declared
     *                     to satisfy the {@link ReportWriter} contract
     * @throws NullPointerException if any argument is {@code null}
     */
    @Override
    public void write(String reportTitle, String[] headers, List<String[]> rows) throws IOException {
        Objects.requireNonNull(reportTitle, "reportTitle cannot be null");
        Objects.requireNonNull(headers, "headers cannot be null");
        Objects.requireNonNull(rows, "rows cannot be null");

        System.out.println();
        System.out.println("=== " + reportTitle + " ===");

        printRow(headers);

        for (String[] row : rows) {
            if (row == null) continue;
            printRow(row);
        }

        System.out.println("=== End of " + reportTitle + " ===");
        System.out.println();
    }

    /**
     * Prints a single row to the console.
     *
     * <p>
     * Columns are separated by {@code " | "}. {@code null} column values are
     * printed as empty strings.
     * </p>
     *
     * @param cols the columns to print
     */
    private void printRow(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(" | ");
            sb.append(cols[i] == null ? "" : cols[i]);
        }
        System.out.println(sb);
    }
}
