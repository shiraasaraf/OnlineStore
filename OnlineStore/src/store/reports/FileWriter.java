package store.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * Writes reports into a CSV file.
 */
public class FileWriter implements ReportWriter {

    private final File outputFile;

    /**
     * @param outputFile destination file (must not be null)
     */
    public FileWriter(File outputFile) {
        this.outputFile = Objects.requireNonNull(outputFile, "outputFile cannot be null");
    }

    @Override
    public void write(String reportTitle, String[] headers, List<String[]> rows) throws IOException {
        Objects.requireNonNull(reportTitle, "reportTitle cannot be null");
        Objects.requireNonNull(headers, "headers cannot be null");
        Objects.requireNonNull(rows, "rows cannot be null");

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            // best-effort directory creation
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

    private String toCsvLine(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');

            String val = (cols[i] == null) ? "" : cols[i];
            sb.append(escapeCsv(val));
        }
        return sb.toString();
    }

    private String escapeCsv(String s) {
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
