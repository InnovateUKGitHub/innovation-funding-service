package com.worth.ifs.security;

import com.worth.ifs.BaseMockSecurityTest;
import com.worth.ifs.commons.security.UidAuthenticationService;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.transactional.FileServiceImpl;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * A test that produces documentation of service calls and their interactions with permission rules
 */
@Ignore
public class DocumentServiceSecurityTest extends BaseMockSecurityTest {

    private List<Class<?>> excludedClasses
            = Arrays.asList(
            UidAuthenticationService.class,
            StatelessAuthenticationFilter.class,
            FileServiceImpl.class
    );

    private List<Class<? extends Annotation>> securityAnnotations
            = Arrays.asList(
            PreAuthorize.class,
            PreFilter.class,
            PostAuthorize.class,
            PostFilter.class
    );

    @Test
    public void generateServiceCallInteractionDocumentation() throws Exception {

        Collection<Object> services = simpleFilter(servicesToTest(), service -> !BaseRestService.class.isAssignableFrom(unwrapProxy(service).getClass()));

        // Assert that we actually have some services.
        assertNotNull(services);
        assertFalse(services.isEmpty());

        // Find all the methods that have a security annotation on and test them
        int totalMethodsChecked = 0;
        for (Object service : services) {
            for (Method method : service.getClass().getMethods()) {
                // Only public methods and not those on base class Object
                if (Modifier.isPublic(method.getModifiers()) && !method.getDeclaringClass().isAssignableFrom(Object.class)) {
                    if (hasOneOf(method, securityAnnotations)) {

                        int parameterCount = method.getParameterCount();
                        Object[] parameters = new Object[parameterCount];
                        for (int i = 0; i < parameterCount; i++) {
                            if (method.getParameterTypes()[i].equals(int.class)) {
                                parameters[i] = 123;
                            } else if (method.getParameterTypes()[i].equals(long.class)) {
                                parameters[i] = 123L;
                            } else if (method.getParameterTypes()[i].equals(double.class)) {
                                parameters[i] = 123d;
                            } else if (method.getParameterTypes()[i].equals(float.class)) {
                                parameters[i] = 123f;
                            } else {
                                parameters[i] = null;
                            }
                        }
                        method.invoke(service, parameters);

                    } else if (hasOneOf(method, singletonList(NotSecured.class))) {

                    }
                }
            }
        }
    }

    @Override
    protected Object createPermissionRuleMock(Object mock, Class<?> mockClass) {
        return createRecordingProxy(mock, mockClass);
    }

    /**
     * Create a pass-through proxy that is able to record interactions with mock objects in a similar way that Mockito does
     * (Mockito does not expose its recordings and so it is necessary to do this manually if you want a list of interactions available)
     *
     * @param instance
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T createRecordingProxy(Object instance, Class<T> clazz) {

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);
        factory.setFilter(method -> AnnotationUtils.findAnnotation(method, PermissionRule.class) != null);

        MethodHandler handler = (self, thisMethod, proceed, args) -> {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + clazz.getSimpleName() + "." + thisMethod.getName());
            return thisMethod.invoke(instance, args);
        };

        try {
            return (T) factory.create(new Class<?>[0], new Object[0], handler);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasOneOf(Method method, List<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> clazz : annotations) {
            if (AnnotationUtils.findAnnotation(method, clazz) != null) {
                return true;
            }
        }
        return false;
    }

    private List<Object> servicesToTest() {
        Collection<Object> services = applicationContext.getBeansWithAnnotation(Service.class).values();
        for (Iterator<Object> i = services.iterator(); i.hasNext(); ) {
            Object service = i.next();
            excludedClasses.stream().filter(exclusion -> unwrapProxy(service).getClass().isAssignableFrom(exclusion)).forEach(exclusion -> {
                i.remove();
            });
        }
        return new ArrayList<>(services);
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
