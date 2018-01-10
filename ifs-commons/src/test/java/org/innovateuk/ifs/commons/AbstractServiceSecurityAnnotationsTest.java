package org.innovateuk.ifs.commons;


import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.evaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.join;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.PermissionRulesClassResult.fromClassAndPermissionMethods;
import static org.innovateuk.ifs.commons.ProxyUtils.*;
import static org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTestUtil.getRulesMap;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.junit.Assert.*;

/**
 * Base class to ensure that all service methods are secured as appropriate.
 */
public abstract class AbstractServiceSecurityAnnotationsTest extends BaseIntegrationTest {


    /**
     * Acceptable {@link Annotation}s to be used at class level, maybe none.
     */
    protected abstract List<Class<? extends Annotation>> classLevelSecurityAnnotations();


    /**
     * Acceptable {@link Annotation}s to be used at method level, maybe none.
     */
    protected abstract List<Class<? extends Annotation>> methodLevelSecurityAnnotations();


    /**
     * The marker {@link Annotation}s on classes that require checking for security Annotation.
     *
     * @return
     */
    protected abstract List<Class<? extends Annotation>> annotationsOnClassesToSecure();


    protected abstract RootCustomPermissionEvaluator evaluator();

    /**
     * Classes that a marker {@link Annotation} provide by the
     * {@link AbstractServiceSecurityAnnotationsTest#annotationsOnClassesToSecure()} method, but should be excluded.
     * One reason for this is that the class in question comes from another package and so there is no control over
     * its use of {@link Annotation}s.
     *
     * @return
     */
    protected abstract List<Class<?>> excludedClasses();

    @Autowired
    protected ApplicationContext context;

    /**
     * Test that all classes and methods that should have security annotations have them.
     * @throws Exception
     */
    @Test
    public void testServiceMethodsHaveSecurityAnnotations() throws Exception {
        Collection<Object> services = servicesToTest();

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        List<Method> notSecured = new ArrayList<>();
        for (Object service : services) {
            // If we are secured at the class level it is not necessary to be secured at the method level.
            if (!isSecuredAtClassLevel(service)) {
                // We are not secured at the class level check at method level.
                notSecured.addAll(notSecuredAtMethodLevel(service));
            }
        }
        if (!notSecured.isEmpty()){
            fail(methodFailureMessage("The following methods need to have a security annotation, or one needs to be added at class level", notSecured));
        }

    }

    /**
     * Find the {@link Method}s on the bean passed in, check whether they need to be secured, and return any that aren't
     * @param service
     * @throws Exception
     */
    private List<Method> notSecuredAtMethodLevel(Object service) throws Exception {
        List<Method> notSecured = new ArrayList<>();
        for (Method method : service.getClass().getMethods()) {
            if (methodNeedsSecuring(method)) {
                if (!hasOneOf(method, methodLevelSecurityAnnotations())) {
                    notSecured.add(method);
                }
            }
        }
        return notSecured;
    }

    /**
     * Test that all methods and classes that need descriptive {@link SecuredBySpring} {@link Annotation}s have them.
     * This is considered to be the case if they have a Spring security {@link Annotation} with simple rules that will
     * not invoke any of the {@link RootCustomPermissionEvaluator} functionality, see
     * {@link AbstractServiceSecurityAnnotationsTest#requiresSecuredBySpringAnnotation}
     *
     * See also {@link AbstractDocumentingServiceSecurityAnnotationsTest}
     *
     * @throws Exception
     */
    @Test
    public void testServiceMethodsHaveSecurityDocumentationAnnotations() throws Exception {
        Collection<Object> services = servicesToTest();

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        List<Class<?>> classLevelFailures = new ArrayList<>();
        List<Method> methodLevelFailures = new ArrayList<>();

        for (Object service : services) {
            // First check at class level
            if (isSecuredAtClassLevel(service)) {
                Class<?> serviceClass = service.getClass();
                // Currently if its secured at class level we need a SecuredBySpring annotation.
                if (!hasOneOf(serviceClass, asList(SecuredBySpring.class))) {
                    classLevelFailures.add(serviceClass);
                }
            }
            // Now for all of the methods
            for (Method method : service.getClass().getMethods()) {
                if (methodNeedsSecuring(method)) {
                    if (requiresSecuredBySpringAnnotation(method) && !hasOneOf(method, asList(SecuredBySpring.class))) {
                        methodLevelFailures.add(method);
                    }
                }
            }
        }

        if (!classLevelFailures.isEmpty() || !methodLevelFailures.isEmpty()){
            // Output all of the errors
            fail(classFailureMessage("The following classes need to have a SecuredBySpring annotation:", classLevelFailures) + "\n" +
                    methodFailureMessage("The following methods need to have a SecuredBySpring annotation:", methodLevelFailures));
        }
    }

    /**
     * It is possible to accidentally secure on a primitive when trying to secure on a resource id.
     * This will appear to work but the problem is that it will open up unexpected permissions.
     * For example say you:
     * Write a permission rule that takes an application id which is a {@link Long}
     * It has a {@link PermissionRule} {@link Annotation} which specifies the permission "READ"
     * Now you have secured {@link Long} NOT application.
     * The next person does the same, but for project.
     * Now the application will allow a user to call the application method which you secured if the project permission
     * rule evaluates to true
     *
     * This test should prevent primitive being secured.
     */
    @Test
    public void testThatResourcesSecuredAreReallyResources(){
        PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap = getRulesMap(evaluator());
        List<PermissionRulesClassResult> results = simpleMap(rulesMap.entrySet(), e -> fromClassAndPermissionMethods(e.getKey(), e.getValue()));
        List<Method> allRulesMethods = flattenLists(simpleMap(results, PermissionRulesClassResult::ruleMethods));
        List<Pair<Method, Class<?>>> allRulesMethodsWithSecuredType = simpleMap(allRulesMethods, m -> Pair.of(m, m.getParameterTypes()[0]));
        List<Pair<Method, Class<?>>> failed = simpleFilter(allRulesMethodsWithSecuredType, p -> !acceptableResourceType(p.getValue()));
        if (!failed.isEmpty()){
            fail("The following methods are protecting primitives not resources:\n" +
                    join(",\n", simpleMap(failed, p -> p.getKey().getName() + " on class " + p.getKey().getDeclaringClass() +
                            " is protecting the primitive: " + p.getValue().getSimpleName())) +
                    "\n If its an id, then instead use a wrapper class for the id, and specify a PermissionEntityLookupStrategies");
        }
    }

    /**
     *
     * @param resource
     * @return
     */
    private boolean acceptableResourceType(Class<?> resource){
        return !(resource.isAssignableFrom(Integer.class) ||
                resource.isAssignableFrom(Long.class) ||
                resource.isAssignableFrom(String.class));
    }

    private String classFailureMessage(String message, List<Class<?>> failures){
        return  message + "\n" + //
                join(",\n", simpleMap(failures, Class::getName));
    }

    private String methodFailureMessage(String message, List<Method> failures){
        return message +  "\n" + join(",\n", simpleMap(failures, m -> m.getName() + " on class " + m.getDeclaringClass()));
    }

    /**
     * Only public methods and not those on base class {@link Object} get checked
     *
     * @param method
     * @return
     */
    private boolean methodNeedsSecuring(Method method) {
        return isPublic(method.getModifiers()) && !isAMethodOnObject(method);
    }

    /**
     * Does the bean passed in have a class level security annotation.
     * @param service
     * @return
     */
    private boolean isSecuredAtClassLevel(Object service) {
        return hasOneOf(service.getClass(), classLevelSecurityAnnotations());
    }

    /**
     * If a security {@link Annotation} uses the default Spring security EL syntax then we need to have a description
     * provided by a {@link SecuredBySpring} {@link Annotation} as we are not able to glean the information any other
     * way.
     *
     * @param method
     * @return
     * @throws Exception
     */
    private boolean requiresSecuredBySpringAnnotation(Method method) throws Exception {
        List<String> values = annotationValues(method, methodLevelSecurityAnnotations());
        return requiresSecuredBySpringAnnotation(values);
    }

    /**
     * We determine whether a Spring security {@link Annotation} will invoke our custom code in the
     * {@link RootCustomPermissionEvaluator} by inspecting its "value" method and checking for standard Spring EL syntax
     * @param values
     * @return
     */
    private boolean requiresSecuredBySpringAnnotation(List<String> values) {
        return !simpleFilter(values, value ->
                value.contains("Authority") ||
                        value.contains("Role") ||
                        value.contains("Authenticated") ||
                        value.contains("Anonymous")).isEmpty();
    }

    /**
     * A {@link List} of the "value" attributes of the annotations specified on the method provided.
     * @param method
     * @param annotationTypes
     * @return
     * @throws Exception
     */
    private List<String> annotationValues(Method method, List<Class<? extends Annotation>> annotationTypes) throws Exception {
        return simpleMap(annotations(method, annotationTypes), a -> valueOf(a));
    }

    /**
     * Grab the "value" from an {@link Annotation}. Will throw if this property does not exist.
     *
     * @param a
     * @return
     */
    private String valueOf(Annotation a) {
        try {
            return (String) a.getClass().getMethod("value").invoke(a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the Spring beans that should be secured.
     * @return
     */
    private Collection<Object> servicesToTest() {
        List<Object> unwrappedServices = allUnwrappedComponentsWithClassAnnotations(context, annotationsOnClassesToSecure());
        return simpleFilter(unwrappedServices, service -> isAssignableFromOneOf(service, excludedClasses()));
    }

    private boolean isAssignableFromOneOf(Object service, List<Class<?>> from) {
        return simpleFilter(from, clazz -> service.getClass().isAssignableFrom(clazz)).isEmpty();
    }
}