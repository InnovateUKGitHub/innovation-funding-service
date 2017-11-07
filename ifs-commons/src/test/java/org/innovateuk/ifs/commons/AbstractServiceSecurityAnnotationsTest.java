package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.ProxyUtils.unwrapProxies;
import static org.innovateuk.ifs.commons.ProxyUtils.unwrapProxy;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.junit.Assert.*;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Base class to ensure that all service methods are secured as appropriate.
 */
public abstract class AbstractServiceSecurityAnnotationsTest extends BaseIntegrationTest {

    protected abstract List<Class<?>> excludedClasses();
    protected abstract List<Class<? extends Annotation>> securityAnnotations();
    protected abstract RootCustomPermissionEvaluator evaluator();

    @Autowired
    protected ApplicationContext context;

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
                    if (!hasOneOf(method, securityAnnotations())) {
                        fail("Method: " + method.getName() + " on class " + method.getDeclaringClass() + " does not have security annotations");
                    }
                    if (needsSecuredBySpring(annotationValues(method, securityAnnotations())) && !hasOneOf(method, asList(SecuredBySpring.class))) {
                        fail("Method: " + method.getName() + " on class " + method.getDeclaringClass() + " needs to have a SecuredBySpring annotation");
                    } else {
                        List<String> annotationValues = annotationValues(method, securityAnnotations());
                        totalMethodsChecked++;
                    }
                }
            }
        }

        // Make sure we are not failing silently
        assertTrue("We should be checking at least one method for security annotations", totalMethodsChecked > 0);
    }

    private boolean needsSecuredBySpring(List<String> values) {
        boolean needsSecuredBySpring = !simpleFilter(values, value ->
                value.contains("Authority") ||
                        value.contains("Role") ||
                        value.contains("Authenticated") ||
                        value.contains("Anonymous")).isEmpty();
        return needsSecuredBySpring;
    }

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

    private List<Object> getApplicableServices() {
        return unwrapProxies(servicesToTest());
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

}
