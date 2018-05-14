package org.innovateuk.ifs.commons;

import au.com.bytecode.opencsv.CSVWriter;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.evaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import static org.innovateuk.ifs.commons.PermissionRulesClassResult.fromClassAndPermissionMethods;
import static org.innovateuk.ifs.commons.ProxyUtils.allUnwrappedComponentsWithClassAnnotations;
import static org.innovateuk.ifs.commons.ProxyUtils.unwrapProxies;
import static org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTestUtil.getRulesMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * There are several ways that security is documented and enforced in the code.
 * This test suite test generates the documentation for three of these mechanisms.
 *
 * Methods can be secured by Spring security annotations which delegate to the {@link RootCustomPermissionEvaluator}.
 * The documentation of these is done by inspecting the internals of {@link RootCustomPermissionEvaluator} and finding
 * the relevant Methods with their {@link PermissionRule} {@link Annotation}.
 *
 * Methods can be secured by Spring security annotations which which use the standard Spring security syntax. This can
 * be done at class or method level. These rules are documented by reading the {@link SecuredBySpring}
 * {@link Annotation}s which are located with the Spring annotations.
 *
 * Finally methods can have a {@link NotSecured} {@link Annotation} which means that either they do not need securing
 * or if {@link NotSecured#mustBeSecuredByOtherServices()} is set to true that there must be a call in the stack above
 * which is secured. The method are also documented here.
 *
 * Note the following points:
 *
 * This documentation does not include the mapping of the secured service methods to the actual
 * customised permission rule methods. There is no good way of generating these on mass and they must be
 * documented by more focused security tests.
 *
 * This test does not enforce the presence of security annotations outlined above,
 * See {@link AbstractServiceSecurityAnnotationsTest} for that.
 *
 */
public abstract class AbstractDocumentingServiceSecurityAnnotationsTest extends BaseIntegrationTest {

    protected abstract List<Class<?>> excludedClasses();
    protected abstract RootCustomPermissionEvaluator evaluator();
    protected abstract List<Class<? extends Annotation>> annotationsOnClassesToSecure();

    @Autowired
    protected ApplicationContext context;


    /**
     * Generate security documentation based on the {@link SecuredBySpring} {@link Annotation}s.
     * These {@link Annotation}s are used on methods and classes to document simple security rules that are enforced
     * by Spring security {@link Annotation}s. They are not required where more complex rules that are processed by
     * the {@link RootCustomPermissionEvaluator}, as these are documented by processing the classes annotated with
     * {@link PermissionRules}
     * @throws Exception
     */
    @Test
    public void generateLowLevelPermissionsDocumentationForSecuredBySpring() throws Exception {
        List<Object> beans = getApplicableServices();
        // Get the results for the Annotations
        List<SecuredBySpringBeanResult> results = simpleMap(beans, bean -> SecuredBySpringBeanResult.fromBean(bean));
        // Format the results for output to a CSV
        SecuredBySpringBeanResultsFormatter formatter = new SecuredBySpringBeanResultsFormatter(results);
        writeCsv("build/secured-by-spring-rules-summary.csv", formatter.simpleHeaders(), formatter.simpleLines());
        writeCsv("build/secured-by-spring-rules-details.csv", formatter.headers(), formatter.lines());
    }

    /**
     * Document the methods that have the {@link NotSecured} {@link Annotation}.
     * @throws Exception
     */
    @Test
    public void generateLowLevelPermissionsDocumentationForNotSecured() throws Exception {
        List<Object> beans = getApplicableServices();
        // Get the results for the Annotations
        List<NotSecuredBeanResult> results = simpleMap(beans, bean -> NotSecuredBeanResult.fromBean(bean));
        // Format the results for output to a CSV
        NotSecuredBeanResultsFormatter formatter = new NotSecuredBeanResultsFormatter(results);
        writeCsv("build/not-secured-rules-summary.csv", formatter.simpleHeaders(), formatter.simpleLines());
        writeCsv("build/not-secured-rules-details.csv", formatter.headers(), formatter.lines());
    }


    /**
     * Document rules that are generated from the custom permission rules classes - i.e. those annotated with
     * {@link PermissionRules}. We use the {@link RootCustomPermissionEvaluator} and reverse engineer the documentation.
     * Doing this means that we don't document anything that isn't present in the running application which would be
     * a risk if we inspected the classpath for {@link PermissionRule}s directly.
     * @throws Exception
     */
    @Test
    public void generateLowLevelPermissionsDocumentationForPermissionRules() throws Exception {
        // Get the internal mappings of classes to be secured to the securing mechanisms.
        PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap = getRulesMap(evaluator());
        List<PermissionRulesClassResult> results = simpleMap(rulesMap.entrySet(), e -> fromClassAndPermissionMethods(e.getKey(), e.getValue()));
        // Format the results for output to CSV
        PermissionRulesClassResultFormatter formatter = new PermissionRulesClassResultFormatter(results);
        writeCsv("build/permission-rules-summary.csv", formatter.simpleHeaders(), formatter.simpleLines());
        writeCsv("build/permission-rules-details.csv", formatter.headers(), formatter.lines());
    }

    private List<Object> getApplicableServices() {
        return unwrapProxies(servicesToTest());
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

    private Collection<Object> servicesToTest() {
        List<Object> unwrappedServices = allUnwrappedComponentsWithClassAnnotations(context, annotationsOnClassesToSecure());
        return simpleFilter(unwrappedServices, service -> isAssignableFromOneOf(service, excludedClasses()));
    }

    private boolean isAssignableFromOneOf(Object service, List<Class<?>> from){
        return simpleFilter(from, clazz ->  service.getClass().isAssignableFrom(clazz)).isEmpty();
    }
}
