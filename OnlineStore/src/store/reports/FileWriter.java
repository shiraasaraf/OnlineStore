/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * CSV file implementation of {@link ReportWriter}.
 *
 * <p>
 * Writes report data into a CSV file using UTF-8 encoding.
 * The first written row is the header row, followed by zero or more data rows.
 * Values are escaped according to basic CSV rules (quotes are doubled and
 * fields that require quoting are wrapped in double quotes).
 * </p>
 */
public class FileWriter implements ReportWriter {

    /** Destination file for the report output. */
    private final File outputFile;

    /**
     * Creates a writer that outputs reports into the given file.
     *
     * @param outputFile destination file (must not be {@code null})
     * @throws NullPointerException if {@code outputFile} is {@code null}
     */
    public FileWriter(File outputFile) {
        this.outputFile = Objects.requireNonNull(outputFile, "outputFile cannot be null");
    }

    /**
     * Writes the report as CSV into the configured output file.
     *
     * <p>
     * If the file parent directory does not exist, a best-effort attempt is made
     * to create it.
     * </p>
     *
     * @param reportTitle report title (must not be {@code null}); not written into the CSV content
     * @param headers     column headers (must not be {@code null})
     * @param rows        data rows (must not be {@code null})
     * @throws IOException if writing the file fails
     * @throws NullPointerException if any argument is {@code null}
     */
    @Override
    public void write(String reportTitle, String[] headers, List<String[]> rows) throws IOException {
        Objects.requireNonNull(reportTitle, "reportTitle cannot be null");
        Objects.requireNonNull(headers, "headers cannot be null");
        Objects.requireNonNull(rows, "rows cannot be null");

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), StandardCharsets.UTF_8)) {
            bw.write(toCsvLine(headers));
            bw.newLine();

            for (String[] row : rows) {
                if (row == null) continue;
                bw.write(toCsvLine(row));
                bw.newLine();
            }
        }
    }

    /**
     * Converts a row of column values into a single CSV line.
     *
     * @param cols the column values
     * @return a CSV-formatted line (without a trailing newline)
     */
    private String toCsvLine(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');

            String val = (cols[i] == null) ? "" : cols[i];
            sb.append(escapeCsv(val));
        }
        return sb.toString();
    }

    /**
     * Escapes a single value for CSV output.
     *
     * <p>
     * Quotes inside the value are doubled. If the value contains a comma, a quote,
     * or a line break, it is wrapped in double quotes.
     * </p>
     *
     * @param s the raw value
     * @return the escaped CSV value
     */
    private String escapeCsv(String s) {
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
