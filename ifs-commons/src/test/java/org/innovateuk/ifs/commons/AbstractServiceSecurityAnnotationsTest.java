package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.ProxyUtils.allUnwrappedComponentsWithClassAnnotations;
import static org.innovateuk.ifs.commons.ProxyUtils.unwrapProxies;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
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
     * @return
     */
    protected abstract List<Class<?>> excludedClasses();

    @Autowired
    protected ApplicationContext context;

    @Test
    public void testServiceMethodsHaveSecurityAnnotations() throws Exception {
        Collection<Object> services = servicesToTest();

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        // Find all the methods that should have a security annotation on and check they do.
        for (Object service : services) {
            if (!testSecuredAtClassLevel(service)) {
                testSecuredAtMethodLevel(service);
            }
        }
    }

    private void testSecuredAtMethodLevel(Object service) throws Exception {
        for (Method method : service.getClass().getMethods()) {
            // Only public methods and not those on base class Object get checked
            if (isPublic(method.getModifiers()) && !method.getDeclaringClass().isAssignableFrom(Object.class)) {
                if (!hasOneOf(method, methodLevelSecurityAnnotations())) {
                    fail("Method: " + method.getName() + " on class " + method.getDeclaringClass() + " does not have security annotations");
                }
                if (needsSecuredBySpring(annotationValues(method, methodLevelSecurityAnnotations())) && !hasOneOf(method, asList(SecuredBySpring.class))) {
                    fail("Method: " + method.getName() + " on class " + method.getDeclaringClass() + " needs to have a SecuredBySpring annotation");
                }
            }
        }
    }

    /**
     *
     * All we do is check if there is a class level security permission present.
     * In future we may want to enforce other behaviour like the use of {@link SecuredBySpring} {@link Annotation}s
     * @param service
     * @return
     */
    private boolean testSecuredAtClassLevel(Object service) {
        return hasOneOf(service.getClass(), classLevelSecurityAnnotations());
    }

    /**
     * If a security {@link Annotation} uses the default Spring security EL syntax then we need to have a description
     * provided by a {@link SecuredBySpring} {@link Annotation} as we are not able to glean the information any other
     * way.
     *
     * @param values
     * @return
     */
    private boolean needsSecuredBySpring(List<String> values) {
        boolean needsSecuredBySpring = !simpleFilter(values, value ->
                value.contains("Authority") ||
                        value.contains("Role") ||
                        value.contains("Authenticated") ||
                        value.contains("Anonymous")).isEmpty();
        return needsSecuredBySpring;
    }

    /**
     * A {@link List} of the value attributes of the annotations specified on the method provided.
     *
     * @param method
     * @param annotations
     * @return
     * @throws Exception
     */
    private List<String> annotationValues(Method method, List<Class<? extends Annotation>> annotations) throws Exception {
        List<String> values = new ArrayList<>();
        for (Class<? extends Annotation> clazz : annotations) {
            // Note that if the annotation does not have a value method this will throw.
            // This should mean that we will not get any silent failures in the event that the signatures change.
            Method valueMethodOnAnnotation = clazz.getDeclaredMethod("value");
            Annotation annotationOnServiceMethod = findAnnotation(method, clazz);
            if (annotationOnServiceMethod != null) {
                String value = (String) valueMethodOnAnnotation.invoke(annotationOnServiceMethod);
                values.add(value);
            }
        }
        return values;
    }

    private Collection<Object> servicesToTest() {
        List<Object> unwrappedServices = allUnwrappedComponentsWithClassAnnotations(context, annotationsOnClassesToSecure());
        return simpleFilter(unwrappedServices, service -> isAssignableFromOneOf(service, excludedClasses()));
    }

    private boolean isAssignableFromOneOf(Object service, List<Class<?>> from){
        return simpleFilter(from, clazz ->  service.getClass().isAssignableFrom(clazz)).isEmpty();
    }

    private boolean hasOneOf(Method method, List<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> clazz : annotations) {
            if (findAnnotation(method, clazz) != null) {
                return true;
            }
        }
        return false;
    }

    private boolean hasOneOf(Class<?> clazz, List<Class<? extends Annotation>> annotations){
        for (Class<? extends Annotation> annotationType : annotations) {
            if (findAnnotation(clazz, annotationType) != null){
                return true;
            }
        }
        return false;
    }
}