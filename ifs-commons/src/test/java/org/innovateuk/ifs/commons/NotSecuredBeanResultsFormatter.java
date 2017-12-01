package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.NotSecured;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generate output that can be easily rendered into a CSV from a {@link List} of {@link NotSecuredBeanResult}
 */
public class NotSecuredBeanResultsFormatter extends AbstractResultsFormatter<NotSecuredBeanResult> {

    public NotSecuredBeanResultsFormatter(List<NotSecuredBeanResult> results){
        super(results);
    }

    protected final List<List<String>> linesFromResult(NotSecuredBeanResult result){
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