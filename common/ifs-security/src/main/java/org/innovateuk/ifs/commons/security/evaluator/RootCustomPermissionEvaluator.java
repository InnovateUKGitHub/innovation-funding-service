package org.innovateuk.ifs.commons.security.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * An implementation of PermissionEvaluator that supports the inclusion of business rules into Spring Security checking
 * via the {@link PermissionRules} and {@link PermissionRule} annotations, and the ability to provide ways of looking up
 * a protected class (e.g. ApplicationResource) from another type (e.g. an ID in the form of a Long) via the
 * {@link PermissionEntityLookupStrategies} and {@link PermissionEntityLookupStrategy} annotations.
 * Note that this does not have the {@link Component} annotation as doing so would force dependent projects to provide
 * the dependencies of this class whether or not it is needed. Instead this class should be subclasses where it is
 * required and the annotation added to that.
 */
@Slf4j
public class RootCustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SecuredMethodsInStackCountInterceptor methodSecuredInStackCountInterceptor;

    @Autowired
    private CustomPermissionEvaluatorTransactionManager transactionManager;

    private PermissionedObjectClassToLookupMethods lookupStrategyMap;

    private PermissionMethodHandler permissionMethodHandler;

    @PostConstruct
    void generateRules() {
        Collection<Object> permissionRuleBeans = applicationContext.getBeansWithAnnotation(PermissionRules.class).values();
        ListOfOwnerAndMethod allRulesMethods = findRules(permissionRuleBeans);

        List<Pair<Object, Method>> failed = failedPermissionMethodSignatures(allRulesMethods);
        if (!failed.isEmpty()) {
            String error = "Permissions methods: " + Arrays.toString(failed.toArray()) + " have an incorrect signature";
            log.error(error);
            throw new IllegalStateException(error); // Fail fast
        }

        PermissionedObjectClassToPermissionsMethods collectedRulesMethods = protectedClassToMethods(allRulesMethods);
        PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap = protectedClassToPermissionToMethods(collectedRulesMethods);

        if (log.isDebugEnabled()) {
            rulesMap.values().forEach(permission -> permission.values().forEach(pairs -> pairs.forEach(pair -> {
                Method permissionMethod = pair.getRight();
                PermissionRule permissionAnnotation = findAnnotation(permissionMethod, PermissionRule.class);
                log.debug("Registered PermissionRule: " + permissionAnnotation.description());
            })));
        }

        permissionMethodHandler = new DefaultPermissionMethodHandler(rulesMap);
    }

    @PostConstruct
    void generateLookupStrategies() {
        final Collection<Object> permissionEntityLookupBeans = applicationContext.getBeansWithAnnotation(PermissionEntityLookupStrategies.class).values();
        final ListOfOwnerAndMethod allLookupStrategyMethods = findLookupStrategies(permissionEntityLookupBeans);
        final PermissionedObjectClassToLookupMethods collectedPermissionLookupMethods = returnTypeToMethods(allLookupStrategyMethods);
        lookupStrategyMap = PermissionedObjectClassToLookupMethods.from(collectedPermissionLookupMethods);
        validate(lookupStrategyMap); // Fail Fast
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetObject, final Object permission) {

        if (methodSecuredInStackCountInterceptor.isStackSecuredAtHigherLevel()) {
            return true;
        }

        if (targetObject == null) {
            return true;
        }

        Class<?> targetClass = targetObject.getClass();

        return transactionManager.doWithinTransaction(() ->
                permissionMethodHandler.hasPermission(authentication, targetObject, permission, targetClass));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        if (methodSecuredInStackCountInterceptor.isStackSecuredAtHigherLevel()) {
            return true;
        }

        final Class<?> clazz;
        try {
            clazz = Class.forName(targetType);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to look up class " + targetType + " that was specified in a @PermissionRule method", e);
        }
        return hasPermission(authentication, targetId, clazz, permission);
    }

    List<String> getPermissions(Authentication authentication, Object targetDomainObject) {
        return permissionMethodHandler.getPermissions(authentication, targetDomainObject);
    }

    private Pair<Object, Method> lookup(final Serializable targetId, final Class<?> targetType) {
        final List<Pair<Object, Method>> allLookupsForTargetClass = lookupStrategyMap.get(targetType);
        if (allLookupsForTargetClass == null) {
            final String error = "No lookups at all found for target class " + targetType + " with target id: " + targetId;
            log.error(error);
            throw new IllegalArgumentException(error);
        }
        final List<Pair<Object, Method>> lookupsForTargetClassAndTargetIdClass = allLookupsForTargetClass.stream()
                .filter(lookupForTargetClass -> {
                    final Method method = lookupForTargetClass.getValue();
                    final boolean methodAcceptsTargetId = method.getParameterTypes()[0].isAssignableFrom(targetId.getClass());
                    return methodAcceptsTargetId;
                }).collect(toList());
        if (lookupsForTargetClassAndTargetIdClass.isEmpty()) {
            final String error = "No lookup found for target class " + targetType + " with target id: " + targetId;
            log.error(error);
            throw new IllegalArgumentException(error);
        } else if (lookupsForTargetClassAndTargetIdClass.size() > 1) {
            final String error = "Multiple lookups found for target class " + targetType + " with target id: " + targetId;
            log.error(error);
            throw new IllegalArgumentException(error);
        }
        return lookupsForTargetClassAndTargetIdClass.get(0);
    }

    List<Pair<Object, Method>> failedPermissionMethodSignatures(ListOfOwnerAndMethod collectedRulesMethods) {
        return collectedRulesMethods.stream().filter(
                beanAndMethod -> {
                    Method method = beanAndMethod.getRight();
                    if (method.getParameterTypes().length == 2) {
                        Class<?> secondParameterClass = method.getParameterTypes()[1];
                        return !Authentication.class.isAssignableFrom(secondParameterClass) &&
                                !UserResource.class.isAssignableFrom(secondParameterClass);
                    }
                    return true;
                }
        ).collect(toList());
    }

    private static final void validate(final PermissionedObjectClassToLookupMethods lookupsStrategyMap) {
        for (final Entry<Class<?>, ListOfOwnerAndMethod> permissionedObjectClassTolookupsStrategies : lookupsStrategyMap.entrySet()) {
            if (!lookupsWithMethodsThatDoNotHaveASingleParameter(permissionedObjectClassTolookupsStrategies.getValue()).isEmpty()) {
                final String error = "Lookups must have a single parameter";
                log.error(error);
                throw new IllegalArgumentException(error);
            }
            if (!lookupsWithMethodsThatDoNotHaveASerializableParameter(permissionedObjectClassTolookupsStrategies.getValue()).isEmpty()) {
                final String error = "Lookups must have a serializable parameter";
                log.error(error);
                throw new IllegalArgumentException(error);
            }
            if (!lookupsWithMethodsThatHaveDuplicateLookupKeyParameter(permissionedObjectClassTolookupsStrategies.getValue()).isEmpty()) {
                final String error = "There must not be any duplicates";
                log.error(error);
                throw new IllegalArgumentException(error);
            }
        }
    }

    private static final ListOfOwnerAndMethod lookupsWithMethodsThatDoNotHaveASingleParameter(final ListOfOwnerAndMethod toCheck) {
        final List<Pair<Object, Method>> matching = toCheck.stream().
                filter(ownerAndMethod -> ownerAndMethod.getValue().getParameterTypes().length != 1)
                .collect(toList());
        return ListOfOwnerAndMethod.from(matching);
    }

    private static final ListOfOwnerAndMethod lookupsWithMethodsThatDoNotHaveASerializableParameter(final ListOfOwnerAndMethod toCheck) {
        final List<Pair<Object, Method>> matching = toCheck.stream().
                filter(ownerAndMethod -> {
                    final Class<?>[] parameterTypes = ownerAndMethod.getValue().getParameterTypes();
                    return parameterTypes.length == 1 && !Serializable.class.isAssignableFrom(parameterTypes[0]);
                })
                .collect(toList());
        return ListOfOwnerAndMethod.from(matching);
    }

    private static final ListOfOwnerAndMethod lookupsWithMethodsThatHaveDuplicateLookupKeyParameter(final ListOfOwnerAndMethod toCheck) {
        final Map<? extends Class<?>, List<Pair<Object, Method>>> keyParameterToLookup = toCheck.stream().
                filter(ownerAndMethod -> ownerAndMethod.getRight().getParameterTypes().length == 1).
                collect(groupingBy(ownerAndMethod -> ownerAndMethod.getRight().getParameterTypes()[0], toList()));
        final List<Pair<Object, Method>> duplicates = keyParameterToLookup.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .flatMap(entry -> entry.getValue().stream()).collect(toList());
        return ListOfOwnerAndMethod.from(duplicates);
    }

    ListOfOwnerAndMethod findRules(Collection<Object> ruleContainingBeans) {
        return findAnnotated(ruleContainingBeans, PermissionRule.class);
    }

    ListOfOwnerAndMethod findLookupStrategies(Collection<Object> permissionEntityLookupBeans) {
        return findAnnotated(permissionEntityLookupBeans, PermissionEntityLookupStrategy.class);
    }


    /**
     * A List of Pairs of owning Objects with Methods for every Method found with the given Annotation.
     * There maybe multiple Pairs with the same owning Object if the owning  Object has multiple Methods with the given annotation.
     *
     * @param owningBeans
     * @param annotation
     * @return
     */
    private static ListOfOwnerAndMethod findAnnotated(final Collection<Object> owningBeans, Class<? extends Annotation> annotation) {
        // Generate a List of owning Objects with a List of all of their Methods which have the annotation.
        final List<Pair<Object, List<Method>>> owningBeansWithAnnotatedMethods = owningBeans.stream()
                .map(owningBean -> Pair.of(owningBean, findAnnotatedMethods(owningBean, annotation)))
                .collect(toList());
        // Flatten this List down so that we have a Pair of owning Object and Method of every Method we have found
        final List<Pair<Object, Method>> owningBeanAndMethodForAllAnnotated = owningBeansWithAnnotatedMethods.stream()
                .flatMap(owningBeanAndMethods -> {
                    final Object owningObject = owningBeanAndMethods.getKey();
                    final List<Method> methods = owningBeanAndMethods.getValue();
                    return transformToOwningObjectAndMethod(owningObject, methods).stream();
                })
                .collect(toList()); //
        return ListOfOwnerAndMethod.from(owningBeanAndMethodForAllAnnotated);
    }

    private static final List<Pair<Object, Method>> transformToOwningObjectAndMethod(final Object owningBean, List<Method> methods) {
        return methods.stream().map(method -> Pair.of(owningBean, method)).collect(toList());
    }

    private static List<Method> findAnnotatedMethods(final Object owningBean, final Class<? extends Annotation> annotation) {
        return asList(owningBean.getClass().getMethods()).stream().filter(method -> findAnnotation(method, annotation) != null).collect(toList());
    }

    PermissionedObjectClassToPermissionsMethods protectedClassToMethods(List<Pair<Object, Method>> allRuleMethods) {
        PermissionedObjectClassToPermissionsMethods map = new PermissionedObjectClassToPermissionsMethods();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getParameterTypes()[0], new ListOfOwnerAndMethod());
            map.get(methodAndBean.getRight().getParameterTypes()[0]).add(methodAndBean);
        }
        return map;
    }

    PermissionedObjectClassToLookupMethods returnTypeToMethods(ListOfOwnerAndMethod allRuleMethods) {
        PermissionedObjectClassToLookupMethods map = new PermissionedObjectClassToLookupMethods();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getReturnType(), new ListOfOwnerAndMethod());
            map.get(methodAndBean.getRight().getReturnType()).add(methodAndBean);
        }
        return map;
    }

    PermissionedObjectClassToPermissionsToPermissionsMethods protectedClassToPermissionToMethods(PermissionedObjectClassToPermissionsMethods protectedClassToMethods) {
        PermissionedObjectClassToPermissionsToPermissionsMethods map = new PermissionedObjectClassToPermissionsToPermissionsMethods();
        for (Entry<Class<?>, ListOfOwnerAndMethod> entry : protectedClassToMethods.entrySet()) {
            for (Pair<Object, Method> methodAndBean : entry.getValue()) {
                Method method = methodAndBean.getRight();
                String permission = findAnnotation(method, PermissionRule.class).value();
                map.putIfAbsent(entry.getKey(), new PermissionsToPermissionsMethods());
                map.get(entry.getKey()).putIfAbsent(permission, new ListOfOwnerAndMethod());
                map.get(entry.getKey()).get(permission).add(methodAndBean);
            }
        }
        return map;
    }

    private boolean hasPermission(Authentication authentication, Serializable targetId, Class<?> targetType, Object permission) {

        return transactionManager.doWithinTransaction(() -> {

            final Pair<Object, Method> lookup = lookup(targetId, targetType);
            final Object permissionEntity;
            try {
                permissionEntity = lookup.getRight().invoke(lookup.getLeft(), targetId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AccessDeniedException("Could not successfully call permission entity lookup method", e);
            }

            if (permissionEntity == null) {
                throw new AccessDeniedException("Could not find entity of type " + targetType + " with id " + targetId);
            }

            return hasPermission(authentication, permissionEntity, permission);
        });
    }
}

