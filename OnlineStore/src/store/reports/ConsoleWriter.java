package store.reports;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Writes reports to the console (stdout) in a simple tabular format.
 */
public class ConsoleWriter implements ReportWriter {

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

    private void printRow(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(" | ");
            sb.append(cols[i] == null ? "" : cols[i]);
        }
        System.out.println(sb);
    }
}
