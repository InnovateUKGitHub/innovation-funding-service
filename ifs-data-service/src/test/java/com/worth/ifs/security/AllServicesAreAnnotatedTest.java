package com.worth.ifs.security;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.security.UidAuthenticationService;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.transactional.FileServiceImpl;
import com.worth.ifs.organisation.transactional.CompanyHouseApiServiceImpl;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static org.junit.Assert.*;

public class AllServicesAreAnnotatedTest extends BaseIntegrationTest {

    @Autowired
    private ApplicationContext context;

    List<Class<?>> excludedClasses
            = Arrays.asList(
                    UidAuthenticationService.class,
                    StatelessAuthenticationFilter.class,
                    FileServiceImpl.class
            );

    List<Class<? extends Annotation>> securityAnnotations
            = Arrays.asList(
                PreAuthorize.class,
                PreFilter.class,
                PostAuthorize.class,
                PostFilter.class,
                NotSecured.class
            );

    @Test
    public void testServiceMethodsHaveSecurityAnnotations() throws Exception {
        Collection<Object> services = simpleFilter(unwrapProxies(servicesToTest()), service -> !BaseRestService.class.isAssignableFrom(service.getClass()));

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

    private boolean hasOneOf(Method method, List<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> clazz : annotations) {
            if (AnnotationUtils.findAnnotation(method, clazz) != null) {
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
            return unwrapProxies(Arrays.asList(services)).get(0);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private List<Object> unwrapProxies(Collection<Object> services) throws Exception {
        List<Object> unwrappedProxies = new ArrayList<>();
        for (Object service : services) {
            if (AopUtils.isJdkDynamicProxy(service)) {
                unwrappedProxies.add(((Advised) service).getTargetSource().getTarget());
            } else {
                unwrappedProxies.add(service);
            }
        }
        return unwrappedProxies;
    }


}
