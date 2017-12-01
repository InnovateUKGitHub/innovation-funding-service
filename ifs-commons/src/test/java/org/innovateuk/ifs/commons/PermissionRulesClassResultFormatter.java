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
 * Generate output that can be easily rendered into a CSV from a {@link List} of {@link PermissionRulesClassResult}
 */
public class PermissionRulesClassResultFormatter extends AbstractResultsFormatter<PermissionRulesClassResult> {

    public PermissionRulesClassResultFormatter(List<PermissionRulesClassResult> results) {
        super(results);
    }

    protected List<List<String>> linesFromResult(PermissionRulesClassResult result){
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