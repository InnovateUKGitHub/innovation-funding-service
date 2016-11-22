package com.worth.ifs.commons.security;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.commons.BaseIntegrationTest;
//import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.security.StatelessAuthenticationFilter;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static com.worth.ifs.util.CollectionFunctions.*;

/**
 * Tests around the Spring Security annotations on the Services and the Permission Rule framework as used by the CustomPermissionEvaluator
 */
public class ServiceSecurityAnnotationsTest extends BaseIntegrationTest {

    public static final String[] SIMPLE_CSV_HEADERS = {"Entity", "Action", "Rule description", "Particular business state where rule is enforced"};
    public static final String[] FULL_CSV_HEADERS = {"Entity", "Action", "Rule description", "Particular business state where rule is enforced", "Rule method", "Additional rule comments"};

    @Autowired
    private ApplicationContext context;

    List<Class<?>> excludedClasses
            = asList(
                    UidAuthenticationService.class,
                    StatelessAuthenticationFilter.class
            );

    List<Class<? extends Annotation>> securityAnnotations
            = asList(
                PreAuthorize.class,
                PreFilter.class,
                PostAuthorize.class,
                PostFilter.class,
                NotSecured.class
            );

    @Test
    public void testServiceMethodsHaveSecurityAnnotations() throws Exception {
        Collection<Object> services = getApplicableServices();

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        // Find all the methods that should have a security annotation on and check they do.
        int totalMethodsChecked = 0;
        for (Object service : services) {
            for (Method method : service.getClass().getMethods()) {
                // Only public methods and not those on base class Object
                if (Modifier.isPublic(method.getModifiers()) && !method.getDeclaringClass().isAssignableFrom(Object.class)) {
                    if (!hasOneOf(method, securityAnnotations)) {
                        fail("Method: " + method.getName() + " on class " + method.getDeclaringClass() + " does not have security annotations");
                    }
                    else {
                        totalMethodsChecked++;
                    }
                }
            }
        }

        // Make sure we are not failing silently
        assertTrue("We should be checking at least one method for security annotations", totalMethodsChecked > 0);
    }

    /**
     * This test will generate a CSV of Permission Rules ordered by the Entities that they apply to.
     *
     * This is of the format "Entity", "Action", "Rule description"
     *
     * @throws Exception
     */
    @Test
    public void generateLowLevelPermissionsDocumentation() throws Exception {

        CustomPermissionEvaluator evaluator = (CustomPermissionEvaluator) context.getBean("customPermissionEvaluator");

        List<String[]> permissionRuleSecuredRows = getPermissionRulesBasedSecurity(evaluator);
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
        writeCsv("build/permission-rules-summary.csv", SIMPLE_CSV_HEADERS, simpleRows);

        // output a more complex csv of rule information
        writeCsv("build/permission-rules-full.csv", FULL_CSV_HEADERS, allSecuredRows);
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
                String[] securedMethodRow = new String[] {entity, action, ruleDescription, ruleState, ruleMethod, ruleComments};
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
                String[] nonSecuredMethodRow = new String[] {entity, action, ruleDescription, ruleMethod, ruleComments};
                return nonSecuredMethodRow;
            });
        });

        return flattenLists(nonSecuredMethodRowsByService);
    }

    //TODO: weird coupling here
    private List<Object> getApplicableServices() {
        return unwrapProxies(servicesToTest());
    }

    private List<String[]> getPermissionRulesBasedSecurity(CustomPermissionEvaluator evaluator) {
        List<String[]> permissionRuleRows = new ArrayList<>();

        CustomPermissionEvaluator.PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap =
                (CustomPermissionEvaluator.PermissionedObjectClassToPermissionsToPermissionsMethods) ReflectionTestUtils.getField(evaluator, "rulesMap");

        Set<Class<?>> securedEntities = rulesMap.keySet();

        securedEntities.forEach(clazz -> {


            Map<String, CustomPermissionEvaluator.ListOfOwnerAndMethod> rulesForSecuringEntity = rulesMap.get(clazz);
            Set<String> actionsSecuredForEntity = rulesForSecuringEntity.keySet();


            actionsSecuredForEntity.forEach(actionName -> {

                CustomPermissionEvaluator.ListOfOwnerAndMethod permissionRuleMethodsForThisAction = rulesForSecuringEntity.get(actionName);

                permissionRuleMethodsForThisAction.forEach(serviceAndMethod -> {

                    Method ruleMethod = serviceAndMethod.getValue();
                    PermissionRule permissionRule = ruleMethod.getDeclaredAnnotation(PermissionRule.class);

                    final String finalClassName = cleanEntityName(clazz);

                    permissionRuleRows.add(new String[] {
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

    };

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
            excludedClasses.stream().filter(exclusion -> unwrapProxy(service).getClass().isAssignableFrom(exclusion)).forEach(exclusion -> {
                i.remove();
            });
        }
        return services;
    }

    private Object unwrapProxy(Object services) {
        try {
            return unwrapProxies(asList(services)).get(0);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private List<Object> unwrapProxies(Collection<Object> services) {
        List<Object> unwrappedProxies = new ArrayList<>();
        for (Object service : services) {
            if (AopUtils.isJdkDynamicProxy(service)) {
                try {
                    unwrappedProxies.add(((Advised) service).getTargetSource().getTarget());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                unwrappedProxies.add(service);
            }
        }
        return unwrappedProxies;
    }


}
