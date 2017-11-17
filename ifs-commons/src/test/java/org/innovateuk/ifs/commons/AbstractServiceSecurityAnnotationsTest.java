package org.innovateuk.ifs.commons;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.join;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.ProxyUtils.*;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

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
     * TODO
     *
     * @throws Exception
     */
    @Test
    public void testServiceMethodsHaveSecurityAnnotations() throws Exception {
        Collection<Object> services = servicesToTest();

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        // Find all the methods that should have a security annotation on and check they do.
        for (Object service : services) {
            // If we are secured at the class level it is not necessary to be secured at the method level.
            if (!isSecuredAtClassLevel(service)) {
                // We are not secured at the class level check at method level.
                testSecuredAtMethodLevel(service);
            }
        }
    }

    private void testSecuredAtMethodLevel(Object service) throws Exception {
        for (Method method : service.getClass().getMethods()) {
            if (methodNeedsSecuring(method)) {
                if (!hasOneOf(method, methodLevelSecurityAnnotations())) {
                    fail("Method: " + method.getName() + " on class " + method.getDeclaringClass() + " does not have security annotations");
                }
            }
        }
    }

    /**
     * TODO
     *
     * @throws Exception
     */
    @Test
    public void testServiceMethodsHaveSecurityDocumentationAnnotations() throws Exception {
        Collection<Object> services = servicesToTest();

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        //
        List<Class<?>> classLevelFailures = new ArrayList<>();
        List<Method> methodLevelFailures = new ArrayList<>();


        for (Object service : services) {
            // First check at class level
            if (isSecuredAtClassLevel(service)) {
                Class<?> serviceClass = service.getClass();
                if (requiresSecuredBySpringAnnotation(serviceClass) && !hasOneOf(serviceClass, asList(SecuredBySpring.class))) {
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
            fail(classFailureMessage(classLevelFailures) + "\n" + methodFailureMessage(methodLevelFailures));
        }
    }

    private String classFailureMessage(List<Class<?>> failures){
        return "The following classes need to have a SecuredBySpring annotation: \n" + //
                join(",\n", simpleMap(failures, Class::getName));
    }

    private String methodFailureMessage(List<Method> failures){
        return "The following methods need to have a SecuredBySpring annotation: \n" + //
                join(",\n", simpleMap(failures, m -> m.getName() + " on class " + m.getDeclaringClass()));
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


    private boolean requiresSecuredBySpringAnnotation(Class<?> clazz) throws Exception {
        List<String> values = annotationValues(clazz, methodLevelSecurityAnnotations());
        return requiresSecuredBySpringAnnotation(values);
    }

    /**
     * TODO
     *
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
     * A {@link List} of the value attributes of the annotations specified on the method provided.
     *
     * @param method
     * @param annotationTypes
     * @return
     * @throws Exception
     */
    private List<String> annotationValues(Method method, List<Class<? extends Annotation>> annotationTypes) throws Exception {
        return simpleMap(annotations(method, annotationTypes), a -> valueOf(a));
    }

    private List<String> annotationValues(Class<?> clazz, List<Class<? extends Annotation>> annotationTypes) throws Exception {
        return simpleMap(annotations(clazz, annotationTypes), a -> valueOf(a));
    }

    /**
     * TODO
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

    private Collection<Object> servicesToTest() {
        List<Object> unwrappedServices = allUnwrappedComponentsWithClassAnnotations(context, annotationsOnClassesToSecure());
        return simpleFilter(unwrappedServices, service -> isAssignableFromOneOf(service, excludedClasses()));
    }

    private boolean isAssignableFromOneOf(Object service, List<Class<?>> from) {
        return simpleFilter(from, clazz -> service.getClass().isAssignableFrom(clazz)).isEmpty();
    }
}