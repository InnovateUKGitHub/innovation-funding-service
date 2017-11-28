package org.innovateuk.ifs.commons;

import au.com.bytecode.opencsv.CSVWriter;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.evaluator.ListOfOwnerAndMethod;
import org.innovateuk.ifs.commons.security.evaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.ProxyUtils.unwrapProxies;
import static org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTestUtil.getRulesMap;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Base class to document security permissions and rules.
 */
public abstract class AbstractDocumentingServiceSecurityAnnotationsTest extends BaseIntegrationTest {

    protected abstract List<Class<?>> excludedClasses();
    protected abstract RootCustomPermissionEvaluator evaluator();

    protected String[] simpleCsvHeaders(){
        return new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced"};
    };

    protected String[] fullCsvHeaders(){
        return new String[]{"Entity", "Action", "Rule description", "Particular business state where rule is enforced", "Rule method", "Additional rule comments"};
    };

    protected String permissionRulesSummaryLocation(){
        return "build/permission-rules-summary.csv";
    }

    protected String permissionRulesFullLocation() {
        return "build/permission-rules-full.csv";
    }

    @Autowired
    protected ApplicationContext context;

    /**
     * There are several ways that security is documented and enforced in the code.
     * This test generates the aggregated documentation for these:
     * <ol>
     *     <li>Goes through all of the classes annotated with {@link PermissionRules} and ascertains from the
     *     contained {@link PermissionRule} annotated methods the rules which can be applied to each entity.</li>
     *     <li>Service methods which are secured with standard Spring rules, and are annotated with
     *     {@link SecuredBySpring} which describes the permission.</li>
     *     <li>Service methods that have the {@link NotSecured} annotation which has a description field of why this is
     *     the case.</li>
     * </ol>
     *
     * This documentation generated is a CSV.
     *
     * Where the permissions are dictated by the custom {@link PermissionRules} class then the format of the CSV is:
     * "Entity", "Action", "Rule description"
     *
     * Note that this documentation does not include the mapping of the secured service methods to the actual
     * customised permission rule methods. There is no good way of generating these on mass and they must be
     * documented by more focused security tests.
     *
     * @throws Exception
     */
    @Test
    public void generateLowLevelPermissionsDocumentation() throws Exception {

        List<String[]> permissionRuleSecuredRows = getPermissionRulesBasedSecurity(evaluator());
        List<String[]> simpleSpringSecuritySecuredRows = getSimpleSpringSecurityBasedSecurity();
        List<String[]> notSecuredRows = getNotSecuredMethods();
        List<String[]> allSecuredRows = combineLists(permissionRuleSecuredRows, simpleSpringSecuritySecuredRows, notSecuredRows);

        allSecuredRows.sort((row1, row2) -> {
            int column1Compare = row1[0].compareTo(row2[0]);
            if (column1Compare != 0) {
                return column1Compare;
            }
            int column2Compare = row1[1].compareTo(row2[1]);
            return column2Compare;
        });

        // output a simple csv of rule information
        List<String[]> simpleRows = simpleMap(allSecuredRows, row -> new String[]{row[0], row[1], row[2], row[3]});
        writeCsv(permissionRulesSummaryLocation(), simpleCsvHeaders(), simpleRows);

        // output a more complex csv of rule information
        writeCsv(permissionRulesFullLocation(), fullCsvHeaders(), allSecuredRows);
    }

    private List<String[]> getSimpleSpringSecurityBasedSecurity() {

        List<Object> services = getApplicableServices();

        List<List<String[]>> securedMethodRowsByService = simpleMap(services, service -> {

            List<Method> serviceMethods = asList(service.getClass().getMethods());
            List<Method> springSecurityAnnotatedMethods = simpleFilter(serviceMethods, method -> hasOneOf(method, singletonList(SecuredBySpring.class)));

            return simpleMap(springSecurityAnnotatedMethods, method -> {
                SecuredBySpring securityAnnotation = AnnotationUtils.findAnnotation(method, SecuredBySpring.class);
                String entity = securityAnnotation.securedType().equals(Void.class) ? "" : cleanEntityName(securityAnnotation.securedType());
                String action = securityAnnotation.value();
                String ruleDescription = securityAnnotation.description();
                String ruleMethod = getMethodCallDescription(method);
                String ruleComments = securityAnnotation.additionalComments();
                String ruleState = securityAnnotation.particularBusinessState();
                String[] securedMethodRow = new String[]{entity, action, ruleDescription, ruleState, ruleMethod, ruleComments};
                return securedMethodRow;
            });
        });

        return flattenLists(securedMethodRowsByService);
    }

    private List<String[]> getNotSecuredMethods() {

        List<Object> services = getApplicableServices();

        List<List<String[]>> nonSecuredMethodRowsByService = simpleMap(services, service -> {

            List<Method> serviceMethods = asList(service.getClass().getMethods());
            List<Method> springSecurityAnnotatedMethods = simpleFilter(serviceMethods, method -> hasOneOf(method, singletonList(NotSecured.class)));

            return simpleMap(springSecurityAnnotatedMethods, method -> {
                NotSecured notSecuredAnnotation = AnnotationUtils.findAnnotation(method, NotSecured.class);
                String entity = ""; //notSecuredAnnotation.securedType().equals(Void.class) ? "" : cleanEntityName(notSecuredAnnotation.securedType());
                String action = ""; //notSecuredAnnotation.value();
                String ruleDescription = notSecuredAnnotation.value(); //notSecuredAnnotation.description();
                String ruleMethod = getMethodCallDescription(method);
                String ruleComments = ""; //notSecuredAnnotation.additionalComments();
                String[] nonSecuredMethodRow = new String[]{entity, action, ruleDescription, ruleMethod, ruleComments};
                return nonSecuredMethodRow;
            });
        });

        return flattenLists(nonSecuredMethodRowsByService);
    }

    private List<Object> getApplicableServices() {
        return unwrapProxies(servicesToTest());
    }

    private List<String[]> getPermissionRulesBasedSecurity(RootCustomPermissionEvaluator evaluator) {
        List<String[]> permissionRuleRows = new ArrayList<>();

        PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap = getRulesMap(evaluator);

        Set<Class<?>> securedEntities = rulesMap.keySet();

        securedEntities.forEach(clazz -> {

            Map<String, ListOfOwnerAndMethod> rulesForSecuringEntity = rulesMap.get(clazz);
            Set<String> actionsSecuredForEntity = rulesForSecuringEntity.keySet();

            actionsSecuredForEntity.forEach(actionName -> {

                ListOfOwnerAndMethod permissionRuleMethodsForThisAction = rulesForSecuringEntity.get(actionName);

                permissionRuleMethodsForThisAction.forEach(serviceAndMethod -> {

                    Method ruleMethod = serviceAndMethod.getValue();
                    PermissionRule permissionRule = ruleMethod.getDeclaredAnnotation(PermissionRule.class);

                    final String finalClassName = cleanEntityName(clazz);

                    permissionRuleRows.add(new String[]{
                            finalClassName,
                            actionName,
                            permissionRule.description(),
                            permissionRule.particularBusinessState(),
                            getMethodCallDescription(ruleMethod),
                            permissionRule.additionalComments()});
                });
            });
        });
        return permissionRuleRows;
    }

    private String getMethodCallDescription(Method ruleMethod) {
        Class<?>[] interfaces = ruleMethod.getDeclaringClass().getInterfaces();
        Class<?> owningClass = interfaces.length > 0 ? interfaces[0] : ruleMethod.getDeclaringClass();
        return owningClass.getSimpleName() + "." + ruleMethod.getName();
    }

    private String cleanEntityName(Class<?> clazz) {
        final String finalClassName;
        if (clazz.getSimpleName().endsWith("Resource")) {
            finalClassName = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - "Resource".length());
        } else {
            finalClassName = clazz.getSimpleName();
        }
        return finalClassName;
    }

    private void writeCsv(String filename, String[] headers, List<String[]> rows) {

        try {
            try (FileWriter fileWriter = new FileWriter(filename)) {

                CSVWriter writer = new CSVWriter(fileWriter, '\t');
                writer.writeNext(headers);
                rows.forEach(writer::writeNext);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean hasOneOf(Method method, List<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> clazz : annotations) {
            if (findAnnotation(method, clazz) != null) {
                return true;
            }
        }
        return false;
    }

    private Collection<Object> servicesToTest() {
        Collection<Object> services = context.getBeansWithAnnotation(Service.class).values();
        for (Iterator<Object> i = services.iterator(); i.hasNext(); ) {
            Object service = i.next();
            excludedClasses().stream().filter(exclusion -> unwrapProxy(service).getClass().isAssignableFrom(exclusion)).forEach(exclusion -> {
                i.remove();
            });
        }
        return services;
    }

    private Object unwrapProxy(Object services) {
        try {
            return unwrapProxies(asList(services)).get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
