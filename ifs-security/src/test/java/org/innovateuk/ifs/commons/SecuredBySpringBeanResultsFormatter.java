package org.innovateuk.ifs.commons;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.SecuredBySpring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.innovateuk.ifs.commons.ProxyUtils.getMethodCallDescription;


/**
 * Generate output that can be easily rendered into a CSV from a {@link List} of {@link SecuredBySpringBeanResult}
 */
public class SecuredBySpringBeanResultsFormatter extends AbstractResultsFormatter<SecuredBySpringBeanResult> {
    
    public SecuredBySpringBeanResultsFormatter(List<SecuredBySpringBeanResult> results) {
        super(results);
    }

    protected List<List<String>> linesFromResult(SecuredBySpringBeanResult result){
        List<List<String>> lines = new ArrayList<>();
        for (Map.Entry<Method, Optional<SecuredBySpring>> entry : result.methodLevel.entrySet()) {
            classLevelLine(result.classLevel, entry.getKey()).map(line -> lines.add(line)); // Only add if classLevel is present
            methodLevelLine(entry).map(line -> lines.add(line)); // Only add if methodLevel is present
        }
        return lines;
    }

    private static Optional<List<String>> classLevelLine(Pair<Class<?>, Optional<SecuredBySpring>> classLevel, Method method){
        return classLevel.getValue().map(securedBySpring -> {
            Class<?> securedType = securedBySpring.securedType();
            String entity = securedType.equals(Void.class) ? "" : securedType.getSimpleName();
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