package org.innovateuk.ifs.commons;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
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
        return classLevel(bean.getClass(), annotationType);

    }

    /**
     * A {@link Pair} of the {@link Class} passed in and the {@link Annotation} of the type provided on that class, if
     * present.
     * @param clazz
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Pair<Class<?>, Optional<T>> classLevel(Class<?> clazz, Class<T> annotationType){
        Optional<T> annotation = ofNullable(findAnnotation(clazz, annotationType));
        return Pair.of(clazz, annotation);
    }

    /**
     * A {@link Pair} of the {@link Class} passed in and a {@link List} of the {@link Annotation}s of the annotation
     * types provided, on that class, where they are present.
     * @param clazz
     * @param annotationTypes
     * @return
     */
    public static Pair<Class<?>, List<? extends Annotation>> classLevel(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes){
        return Pair.of(clazz, flattenOptional(simpleMap(annotationTypes, a -> classLevel(clazz, a).getValue())));
    }

    /**
     * True if the passed in {@link Class} has at least one class level {@link Annotation} of the annotation types
     * provided.
     * @param clazz
     * @param annotationTypes
     * @return
     */
    public static boolean hasOneOf(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes){
        return !classLevel(clazz, annotationTypes).getValue().isEmpty();
    }

    /**
     * True if the passed in {@link Method} has at least one {@link Annotation} of the annotation types provided.
     * @param method
     * @param annotationTypes
     * @return
     */
    public static boolean hasOneOf(Method method, Collection<Class<? extends Annotation>> annotationTypes){
        return !annotations(method, annotationTypes).isEmpty();
    }

    /**
     * A {@link Map} with the {@link Method}s on the {@link Class} of the bean provided as the keys and the
     * {@link Annotation} of the type provided on the associated method as the values, if present.
     * @param bean
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Map<Method, Optional<T>> methodLevel(Object bean, Class<T> annotationType){
        return getMethods(bean).stream().collect(toMap(identity(), method -> annotation(method, annotationType)));
    }

    /**
     * {@link Optional} containing the {@link Annotation} of the annotation type provided if it exists on the
     * {@link Method} provided.
     * @param method
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Optional<T> annotation(Method method, Class<T> annotationType){
        return ofNullable(findAnnotation(method, annotationType));
    }

    /**
     * {@link Optional} containing the {@link Annotation} of the annotation type provided if it exists at class level on
     * the {@link Class} provided.
     * @param clazz
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Optional<T> annotation(Class clazz, Class<T> annotationType){
        return ofNullable(findAnnotation(clazz, annotationType));
    }

    /**
     * {@link List} containing the {@link Annotation}s of the annotation types provided if they exists on the
     * {@link Method} given.
     * @param method
     * @param annotationTypes
     * @return
     */
    public static List<? extends Annotation> annotations(Method method, Collection<Class<? extends Annotation>> annotationTypes){
        return flattenOptional(simpleMap(annotationTypes, a -> annotation(method, a)));
    }

    /**
     * {@link List} containing the {@link Annotation}s of the annotation types provided if they exists at class levle on
     * the {@link Method} given.
     * @param clazz
     * @param annotationTypes
     * @return
     */
    public static List<? extends Annotation> annotations(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes){
        return flattenOptional(simpleMap(annotationTypes, a -> annotation(clazz, a)));
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

    /**
     * Is the {@link Method} in question is defined on {@link Object}
     * @param method
     * @return
     */
    public static boolean isAMethodOnObject(Method method){
        return method.getDeclaringClass().isAssignableFrom(Object.class);
    }

    /**
     * Conveience function to get a {@link List} of {@link Method}s from bean as opposed to an {@link Array}
     *
     * @param bean
     * @return
     */
    private static List<Method> getMethods(Object bean){
        return stream(bean.getClass().getMethods()).collect(toList());
    }
}
