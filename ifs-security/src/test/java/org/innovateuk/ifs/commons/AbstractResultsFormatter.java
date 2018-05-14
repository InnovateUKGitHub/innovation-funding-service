package org.innovateuk.ifs.commons;


import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generate output that can be easily rendered into a CSV from a {@link List} of {@link NotSecuredBeanResult}
 */
public abstract class AbstractResultsFormatter<T> {

    protected final List<T> results;

    private static final String[] SIMPLE_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced"};

    private static final String[] FULL_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced", "Rule method", "Additional rule comments"};

    public AbstractResultsFormatter(List<T> results){
        this.results = results;
    }

    public final String[] simpleHeaders(){
        return SIMPLE_CSV_HEADERS;
    }

    public final String[] headers(){
        return FULL_CSV_HEADERS;
    }

    public final List<String[]> simpleLines(){
        return simpleMap(lines(), row -> new String[]{row[0], row[1], row[2], row[3]});
    }

    public final List<String[]> lines(){
        List<List<String>> lines = format(results);
        return simpleMap(lines, line -> line.toArray(new String[line.size()]));
    }

    protected final List<List<String>> format(List<T> results) {
        return flattenLists(simpleMap(results, this::format));
    }

    protected final List<List<String>> format(T result) {
        return linesFromResult(result);
    }

    protected abstract List<List<String>> linesFromResult(T result);
}