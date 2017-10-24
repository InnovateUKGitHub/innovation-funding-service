package org.innovateuk.ifs;

import au.com.bytecode.opencsv.CSVWriter;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.evaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import org.innovateuk.ifs.security.evaluator.RootCustomPermissionEvaluator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTestUtil.getRulesMap;

/**
 * Find all the endpoints in the WebApplicationContext and their security settings,
 * and write them to a file.
 *
 * Each web application requiring the documentation should extend this class, without
 * requiring any additional code.
 */
public abstract class EndpointDocumentationTest extends BaseIntegrationTest {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    RootCustomPermissionEvaluator evaluator;

    private void writeCsv(String filename, String[] headers, List<String[]> rows) {
        try {
            try (FileWriter fileWriter = new FileWriter(filename)) {
                CSVWriter writer = new CSVWriter(fileWriter, ',');
                writer.writeNext(headers);
                rows.forEach(writer::writeNext);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * Get a list of properties (name, description, state, comments) for all permission rules
     * defined in the given PermissionEvaluator
     */
    private List<String[]> getPermissionRules(RootCustomPermissionEvaluator evaluator) {
        List<String[]> permissionRuleRows = new ArrayList<>();

        PermissionedObjectClassToPermissionsToPermissionsMethods ruleMap = getRulesMap(evaluator);

        ruleMap.values().forEach(mapEntry -> {
            mapEntry.entrySet().forEach(ruleEntry -> {
                ruleEntry.getValue().forEach(serviceAndMethod -> {
                    Method ruleMethod = serviceAndMethod.getValue();
                    PermissionRule permissionRule = ruleMethod.getDeclaredAnnotation(PermissionRule.class);
                    permissionRuleRows.add(new String[] {
                            ruleEntry.getKey(),
                            permissionRule.description(),
                            permissionRule.particularBusinessState(),
                            permissionRule.additionalComments()});
                });
            });
        });
        return permissionRuleRows;
    }

    private List<String[]> generateInfoForMapping(RequestMappingInfo requestInfo, String constraint, String[] rule, String contextPath) {
        List<String[]> rows = new ArrayList<>();
        String ruleString = (constraint != null) ? constraint : "";
        String ruleName = (rule != null) ? rule[0] : "";
        String ruleDesc = (rule != null) ? rule[1] : "";
        Set<String> path = requestInfo.getPatternsCondition().getPatterns();
        path.forEach(p -> {
            requestInfo.getMethodsCondition().getMethods().forEach(httpMethod -> {
                StringBuilder urlParameters = new StringBuilder();
                for (NameValueExpression<String> expression : requestInfo.getParamsCondition().getExpressions()) {
                    if (urlParameters.length() == 0) {
                        urlParameters.append("?");
                    } else if (urlParameters.length() > 0) {
                        urlParameters.append("&");
                    }
                    urlParameters.append(expression.getName()).append("={").append(expression.getName()).append("}");
                };

                rows.add(new String[] {contextPath+p+urlParameters.toString(), httpMethod.toString(), ruleString, ruleName, ruleDesc});
            });
        });
        return rows;
    }

    @Test
    public void documentEndPoints() {
        String contextPath = applicationContext.getServletContext().getContextPath();
        List<String[]> rules = getPermissionRules(evaluator);
        List<String[]> rows = new ArrayList<>();

        for (Map.Entry<RequestMappingInfo,HandlerMethod> method : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            PreAuthorize controllerAnnotation = AnnotationUtils.findAnnotation(method.getValue().getBeanType(), PreAuthorize.class);
            PreAuthorize methodAnnotation = AnnotationUtils.findAnnotation(method.getValue().getMethod(), PreAuthorize.class);
            if (methodAnnotation != null) {
                boolean foundRule = false;
                for (String[] rule : rules) {
                    if (methodAnnotation.value().contains(rule[0])) {
                        rows.addAll(generateInfoForMapping(method.getKey(), methodAnnotation.value(), rule, contextPath));
                        foundRule = true;
                    }
                }
                if (!foundRule) {
                    rows.addAll(generateInfoForMapping(method.getKey(), methodAnnotation.value(), null, contextPath));
                }
            } else if (controllerAnnotation != null) {
                rows.addAll(generateInfoForMapping(method.getKey(), controllerAnnotation.value(), null, contextPath));
            } else {
                rows.addAll(generateInfoForMapping(method.getKey(), "", null, contextPath));
            }
        }

        String serviceName = !contextPath.equals("") ? contextPath : "root";
        String filename = "build/"+serviceName+"-endpoints.csv";
        String[] header = new String[] {"path", "method", "constraint", "rule", "description"};
        writeCsv(filename, header, rows);
    }
}
