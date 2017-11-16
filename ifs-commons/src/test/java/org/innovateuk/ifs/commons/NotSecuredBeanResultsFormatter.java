package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.NotSecured;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generate output that can be easily rendered into a CSV from a {@link List} of {@link NotSecuredBeanResult}
 */
public class NotSecuredBeanResultsFormatter {

    private final List<NotSecuredBeanResult> results;

    private static final String[] SIMPLE_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced"};

    private static final String[] FULL_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced", "Rule method", "Additional rule comments"};

    public NotSecuredBeanResultsFormatter(List<NotSecuredBeanResult> results){
        this.results = results;
    }

    public String[] simpleHeaders(){
        return SIMPLE_CSV_HEADERS;
    }

    public String[] headers(){
        return FULL_CSV_HEADERS;
    }

    public List<String[]> simpleLines(){
        return simpleMap(lines(), row -> new String[]{row[0], row[1], row[2], row[3]});
    }

    public List<String[]> lines(){
        List<List<String>> lines = format(results);
        return simpleMap(lines, line -> line.toArray(new String[line.size()]));
    }

    private List<List<String>> format(List<NotSecuredBeanResult> results) {
        return flattenLists(simpleMap(results, this::format));
    }

    private List<List<String>> format(NotSecuredBeanResult result) {
        return linesFromResult(result);
    }

    private static List<List<String>> linesFromResult(NotSecuredBeanResult result){
        return simpleMap(result.methodLevel.entrySet(), entry -> {
            NotSecured notSecuredAnnotation = entry.getValue();
            String entity = "";
            String action = "";
            String ruleDescription = notSecuredAnnotation.value();
            String particularBusinessState = "";
            String ruleMethod = ProxyUtils.getMethodCallDescription(entry.getKey());
            String ruleComments = "";
            return asList(entity, action, ruleDescription, particularBusinessState, ruleMethod, ruleComments);
        });
    }
}