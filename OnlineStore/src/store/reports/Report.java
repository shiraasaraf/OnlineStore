package store.reports;

import store.engine.StoreEngine;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Base abstraction for reports.
 * Concrete reports define the content; the writer defines the output mechanism.
 */
public abstract class Report {

    private final ReportWriter writer;

    /**
     * @param writer output writer implementation (must not be null)
     */
    protected Report(ReportWriter writer) {
        this.writer = Objects.requireNonNull(writer, "writer cannot be null");
    }

    /**
     * Generates the report using the provided engine data and delegates writing to the writer.
     *
     * @param engine store engine (must not be null)
     * @throws IOException if writing fails
     */
    public final void generate(StoreEngine engine) throws IOException {
        Objects.requireNonNull(engine, "engine cannot be null");
        writer.write(getTitle(), getHeaders(), buildRows(engine));
    }

    /**
     * @return report title
     */
    protected abstract String getTitle();

    /**
     * @return CSV headers
     */
    protected abstract String[] getHeaders();

    /**
     * Builds the report rows from the store engine data.
     *
     * @param engine store engine
     * @return list of rows; each row is a String[] aligned with headers
     */
    protected abstract List<String[]> buildRows(StoreEngine engine);
}
