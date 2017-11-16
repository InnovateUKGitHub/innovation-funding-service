package org.innovateuk.ifs.commons;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.SecuredBySpring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.innovateuk.ifs.commons.ProxyUtils.getMethodCallDescription;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * TODO
 */
public class SecuredBySpringBeanResultsFormatter {

    private final List<SecuredBySpringBeanResult> results;

    private static final String[] SIMPLE_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced"};

    private static final String[] FULL_CSV_HEADERS
            = new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced", "Rule method", "Additional rule comments"};

    public SecuredBySpringBeanResultsFormatter(List<SecuredBySpringBeanResult> results){
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

    private List<List<String>> format(List<SecuredBySpringBeanResult> results) {
        return flattenLists(simpleMap(results, this::format));
    }

    private List<List<String>> format(SecuredBySpringBeanResult result) {
        return linesFromResult(result);
    }

    private static List<List<String>> linesFromResult(SecuredBySpringBeanResult result){
        List<List<String>> lines = new ArrayList<>();
        for (Map.Entry<Method, Optional<SecuredBySpring>> entry : result.methodLevel.entrySet()) {
            classLevelLine(result.classLevel, entry.getKey()).map(line -> lines.add(line)); // Only add if classLevel is present
            methodLevelLine(entry).map(line -> lines.add(line)); // Only add if methodLevel is present
        }
        return lines;
    }

    private static Optional<List<String>> classLevelLine(Pair<Class<?>, Optional<SecuredBySpring>> classLevel, Method method){
        return classLevel.getValue().map(securedBySpring -> {
            String entity = "";
            String action = securedBySpring.value();
            String ruleDescription = securedBySpring.description();
            String ruleState = securedBySpring.particularBusinessState();
            String ruleMethod = getMethodCallDescription(method);
            String ruleComments = securedBySpring.additionalComments();
            return asList(entity, action, ruleDescription, ruleState, ruleMethod, ruleComments);
        });
    }

    private static Optional<List<String>> methodLevelLine(Map.Entry<Method, Optional<SecuredBySpring>> methodLevel){
        return methodLevel.getValue().map(securedBySpring -> {
            Class<?> securedType = securedBySpring.securedType();
            String entity = securedType.equals(Void.class) ? "" : removeEnd(securedType.getSimpleName(), "Resource");
            String action = securedBySpring.value();
            String ruleDescription = securedBySpring.description();
            String ruleState = securedBySpring.particularBusinessState();
            String ruleMethod = getMethodCallDescription(methodLevel.getKey());
            String ruleComments = securedBySpring.additionalComments();
            return asList(entity, action, ruleDescription, ruleState, ruleMethod, ruleComments);
        });
    }
}