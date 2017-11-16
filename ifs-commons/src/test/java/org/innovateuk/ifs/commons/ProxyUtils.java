package org.innovateuk.ifs.commons;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Utility class to help with Spring wrapped proxies and other reflection methods
 */
public class ProxyUtils {

    public static List<Object> unwrapProxies(Collection<Object> services) {
        return simpleMap(services, ProxyUtils::unwrapProxy);
    }

    /**
     * Extract the original class from a Spring wrapped proxy. Called recursively to peel away all advice.
     * @param service
     * @return
     */
    public static Object unwrapProxy(Object service){
       if (AopUtils.isAopProxy(service)){
           try {
               // Recursively peel away advice.
               return unwrapProxy(((Advised) service).getTargetSource().getTarget());
           }
           catch (Exception e){
               throw new RuntimeException(e);
           }
       } else {
            return service;
       }
    }

    /**
     * Given an {@link ApplicationContext} find all components which have the given class {@link Annotation}s and return
     * them unwrapped.
     * @param context
     * @param withClassAnnotations
     * @return
     */
    public static List<Object> allUnwrappedComponentsWithClassAnnotations(ApplicationContext context, List<Class<? extends Annotation>> withClassAnnotations){
        List<Object> wrappedServices = flattenLists(withClassAnnotations, a -> context.getBeansWithAnnotation(a).values());
        List<Object> unwrappedServices = unwrapProxies(wrappedServices);
        return  unwrappedServices;
    }

    /**
     * A {@link Pair} of the {@link Class} of the bean passed in and the {@link Annotation} of the type provided on that
     * class, if present.
     * @param bean
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Pair<Class<?>, Optional<T>> classLevel(Object bean, Class<T> annotationType){
        Class<?> beanClass = bean.getClass();
        Optional<T> annotation = ofNullable(findAnnotation(bean.getClass(), annotationType));
        return Pair.of(beanClass, annotation);
    }

    /**
     * A {@link Map} with the {@link Method}s on the {@link Class} of the bean provided as the keys and the
     * {@link Annotation} of the type provided on the associated method as the values, if present.
     * @param bean
     * @param annotationType
     * @param <T>
     * @return
     */
    public static final <T extends Annotation> Map<Method, Optional<T>> methodLevel(Object bean, Class<T> annotationType){
        return getMethods(bean).stream().collect(toMap(identity(), method -> ofNullable(findAnnotation(method, annotationType))));
    }

    /**
     * The method name proceeded by either the class or the interface name, the latter when the method is defined on
     * an interface.
     * @param method
     * @return
     */
    public static String getMethodCallDescription(Method method) {
        Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
        Class<?> owningClass = interfaces.length > 0 ? interfaces[0] : method.getDeclaringClass();
        return owningClass.getSimpleName() + "." + method.getName();
    }

    public static boolean isAMethodOnObject(Method method){
        return method.getDeclaringClass().isAssignableFrom(Object.class);
    }

    public static List<Method> getMethods(Object bean){
        return stream(bean.getClass().getMethods()).collect(toList());
    }
}
