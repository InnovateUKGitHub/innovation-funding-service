package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.PermissionRule;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.innovateuk.ifs.commons.ProxyUtils.getMethodCallDescription;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * TODO
 */
public class PermissionRulesClassResultFormatter {

    private final List<PermissionRulesClassResult> results;

    private static final String[] SIMPLE_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced"};

    private static final String[] FULL_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced", "Rule method", "Additional rule comments"};

    public PermissionRulesClassResultFormatter(List<PermissionRulesClassResult> results){
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

    private List<List<String>> format(List<PermissionRulesClassResult> results) {
        return flattenLists(simpleMap(results, this::format));
    }

    private List<List<String>> format(PermissionRulesClassResult result) {
        return linesFromResult(result);
    }

    private static List<List<String>> linesFromResult(PermissionRulesClassResult result){
        return simpleMap(result.rules, methodAndAnnotation -> {
            PermissionRule rule = methodAndAnnotation.getValue();
            Method method = methodAndAnnotation.getKey();
            String entity = removeEnd(result.securedClass.getSimpleName(), "Resource");
            String action = rule.value();
            String ruleDescription = rule.description();
            String particularBusinessState = rule.particularBusinessState();
            String ruleMethod = getMethodCallDescription(method);
            String ruleComments = rule.additionalComments();
            return asList(entity, action, ruleDescription, particularBusinessState, ruleMethod, ruleComments);
        });
    }
}