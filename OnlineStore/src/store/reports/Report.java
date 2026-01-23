/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.reports;

import store.engine.StoreEngine;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all reports.
 *
 * <p>
 * This class represents the <b>Abstraction</b> in the Bridge design pattern.
 * Concrete subclasses define the report content (title, headers, and rows),
 * while the output mechanism is delegated to a {@link ReportWriter}
 * implementation (the <b>Implementor</b>).
 * </p>
 */
public abstract class Report {

    /** Writer used to output the report. */
    private final ReportWriter writer;

    /**
     * Constructs a report with the given output writer.
     *
     * @param writer the writer responsible for outputting the report
     * @throws NullPointerException if {@code writer} is {@code null}
     */
    protected Report(ReportWriter writer) {
        this.writer = Objects.requireNonNull(writer, "writer cannot be null");
    }

    /**
     * Generates the report using data from the provided store engine.
     *
     * <p>
     * This method defines the fixed report-generation workflow:
     * </p>
     * <ol>
     *   <li>Retrieve the report title</li>
     *   <li>Retrieve the report headers</li>
     *   <li>Build the report rows from the engine data</li>
     *   <li>Delegate output to the configured {@link ReportWriter}</li>
     * </ol>
     *
     * @param engine the store engine providing report data
     * @throws IOException if writing the report fails
     * @throws NullPointerException if {@code engine} is {@code null}
     */
    public final void generate(StoreEngine engine) throws IOException {
        Objects.requireNonNull(engine, "engine cannot be null");
        writer.write(getTitle(), getHeaders(), buildRows(engine));
    }

    /**
     * Returns the report title.
     *
     * @return the report title
     */
    protected abstract String getTitle();

    /**
     * Returns the column headers for the report.
     *
     * @return an array of header names
     */
    protected abstract String[] getHeaders();

    /**
     * Builds the report rows using data from the store engine.
     *
     * <p>
     * Each returned row must align with the order and number of columns
     * defined by {@link #getHeaders()}.
     * </p>
     *
     * @param engine the store engine providing access to data
     * @return a list of rows, where each row is a {@code String[]} of column values
     */
    protected abstract List<String[]> buildRows(StoreEngine engine);
}
