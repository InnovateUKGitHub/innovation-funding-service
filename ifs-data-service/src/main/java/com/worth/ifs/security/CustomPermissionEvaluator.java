package com.worth.ifs.security;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Log LOG = LogFactory.getLog(CustomPermissionEvaluator.class);

    private static final UserResource ANONYMOUS_USER = new UserResource();

    @Autowired
    private ApplicationContext applicationContext;

    private DtoClassToPermissionsToPermissionsMethods rulesMap;

    private DtoClassToLookupMethod lookupStrategyMap;

    public static boolean isAnonymous(UserResource user) {
        return user == ANONYMOUS_USER;
    }

    @PostConstruct
    void generateRules() {
        Collection<Object> permissionRuleBeans = applicationContext.getBeansWithAnnotation(PermissionRules.class).values();
        ListOfOwnerAndMethod allRulesMethods = findRules(permissionRuleBeans);

        List<Pair<Object, Method>> failed = failedPermissionMethodSignatures(allRulesMethods);
        if (!failed.isEmpty()) {
            String error = "Permissions methods: " + Arrays.toString(failed.toArray()) + " have an incorrect signature";
            LOG.error(error);
            throw new IllegalStateException(error); // Fail fast
        }

        DtoClassToPermissionsMethods collectedRulesMethods = dtoClassToMethods(allRulesMethods);
        rulesMap = dtoClassToPermissionToMethods(collectedRulesMethods);

        if (LOG.isDebugEnabled()) {
            rulesMap.values().forEach(permission -> permission.values().forEach(pairs -> pairs.forEach(pair -> {
                Method permissionMethod = pair.getRight();
                PermissionRule permissionAnnotation = findAnnotation(permissionMethod, PermissionRule.class);
                LOG.debug("Registered PermissionRule: " + permissionAnnotation.description());
            })));
        }
    }

    @PostConstruct
    void generateLookupStrategies() {
        Collection<Object> permissionEntityLookupBeans = applicationContext.getBeansWithAnnotation(PermissionEntityLookupStrategies.class).values();
        ListOfOwnerAndMethod allLookupStrategyMethods = findLookupStrategies(permissionEntityLookupBeans);

        List<Pair<Object, Method>> failed = failedLookupMethodSignatures(allLookupStrategyMethods);
        if (!failed.isEmpty()) {
            String error = "Lookup methods: " + Arrays.toString(failed.toArray()) + " have an incorrect signature";
            LOG.error(error);
            throw new IllegalStateException(error); // Fail fast
        }

        DtoClassToLookupMethods collectedPermissionLookupMethods = returnTypeToMethods(allLookupStrategyMethods);
        lookupStrategyMap = DtoClassToLookupMethod.from(collectedPermissionLookupMethods.entrySet().stream().
                map(entry -> of(entry.getKey(), getOnlyElement(entry.getValue()))).
                collect(toMap(Pair::getLeft, Pair::getRight)));
    }

    List<Pair<Object, Method>> failedPermissionMethodSignatures(ListOfOwnerAndMethod collectedRulesMethods) {
        return collectedRulesMethods.stream().filter(
                beanAndMethod -> {
                    Method method = beanAndMethod.getRight();
                    if (method.getParameterTypes().length == 2) {
                        Class<?> secondParameterClass = method.getParameterTypes()[1];
                        if (Authentication.class.isAssignableFrom(secondParameterClass) ||
                                UserResource.class.isAssignableFrom(secondParameterClass)) {
                            return false;
                        }
                    }
                    return true;
                }
        ).collect(toList());
    }

    List<Pair<Object, Method>> failedLookupMethodSignatures(ListOfOwnerAndMethod collectedLookupMethods) {
        return collectedLookupMethods.stream().filter(
                beanAndMethod -> {
                    Method method = beanAndMethod.getRight();
                    if (method.getParameterTypes().length == 1) {
                        Class<?> firstParameterClass = method.getParameterTypes()[0];
                        if (Serializable.class.isAssignableFrom(firstParameterClass)) {
                            return false;
                        }
                    }
                    return true;
                }
        ).collect(toList());
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

    DtoClassToPermissionsMethods dtoClassToMethods(List<Pair<Object, Method>> allRuleMethods) {
        DtoClassToPermissionsMethods map = new DtoClassToPermissionsMethods();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getParameterTypes()[0], new ListOfOwnerAndMethod());
            map.get(methodAndBean.getRight().getParameterTypes()[0]).add(methodAndBean);
        }
        return map;
    }

    DtoClassToLookupMethods returnTypeToMethods(ListOfOwnerAndMethod allRuleMethods) {
        DtoClassToLookupMethods map = new DtoClassToLookupMethods();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getReturnType(), new ListOfOwnerAndMethod());
            map.get(methodAndBean.getRight().getReturnType()).add(methodAndBean);
        }
        return map;
    }


    DtoClassToPermissionsToPermissionsMethods dtoClassToPermissionToMethods(DtoClassToPermissionsMethods dtoClassToMethods) {
        DtoClassToPermissionsToPermissionsMethods map = new DtoClassToPermissionsToPermissionsMethods();
        for (Entry<Class<?>, ListOfOwnerAndMethod> entry : dtoClassToMethods.entrySet()) {
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

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetDomainObject, final Object permission) {

        if (targetDomainObject == null) {
            return true;
        }

        Class<?> dtoClass = targetDomainObject.getClass();
        ListOfOwnerAndMethod methodsToCheck =
                rulesMap.getOrDefault(dtoClass, emptyPermissions())
                        .getOrDefault(permission, emptyMethods());
        return methodsToCheck.stream().map(
                methodAndBean -> callHasPermissionMethod(methodAndBean, targetDomainObject, authentication)
        ).reduce(
                false, (a, b) -> a || b
        );
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, Class<?> targetType, Object permission) {
        Pair<Object, Method> lookupMethod = lookupStrategyMap.get(targetType);

        if (lookupMethod == null || lookupMethod.getRight() == null) {
            throw new IllegalArgumentException("Could not find lookup mechanism for type " + targetType + ".  Should be a method annotated " +
                    "with @PermissionEntityLookupStrategy within a class annotated with @PermissionEntityLookupStrategies");
        }

        final Object permissionEntity;

        try {
            permissionEntity = lookupMethod.getRight().invoke(lookupMethod.getLeft(), targetId);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AccessDeniedException("Could not successfully call permission entity lookup method", e);
        }

        if (permissionEntity == null) {
            throw new AccessDeniedException("Could not find entity of type " + targetType + " with id " + targetId);
        }

        return hasPermission(authentication, permissionEntity, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(targetType);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to look up class " + targetType + " that was specified in a @PermissionRule method", e);
        }
        return hasPermission(authentication, targetId, clazz, permission);
    }

    private boolean callHasPermissionMethod(Pair<Object, Method> methodAndBean, Object dto, Authentication authentication) {

        final Object finalAuthentication;

        Method method = methodAndBean.getValue();
        Class<?> secondParameter = method.getParameterTypes()[1];

        if (secondParameter.equals(UserResource.class)) {
            if (authentication instanceof UserAuthentication) {
                finalAuthentication = ((UserAuthentication) authentication).getDetails();
            } else if (authentication instanceof AnonymousAuthenticationToken) {
                finalAuthentication = ANONYMOUS_USER;
            } else {
                throw new IllegalArgumentException("Unable to determine the authentication token for Spring Security");
            }
        } else if (Authentication.class.isAssignableFrom(secondParameter)) {
            finalAuthentication = authentication;
        } else {
            throw new IllegalArgumentException("Second parameter of @PermissionRule-annotated method " + method.getName() + " should be " +
                    "either an instance of " + UserResource.class.getName() + " or an org.springframework.security.core.Authentication implementation, " +
                    "but was " + secondParameter.getName());
        }

        try {
            return (Boolean) method.invoke(methodAndBean.getLeft(), dto, finalAuthentication);
        } catch (Exception e) {
            LOG.error("Error whilst processing a permissions method", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getPermissions(Authentication authentication, Object targetDomainObject) {
        return rulesMap.getOrDefault(targetDomainObject.getClass(), emptyPermissions()).keySet().stream().filter(
                permission -> hasPermission(authentication, targetDomainObject, permission)
        ).sorted().collect(toList());
    }

    public List<String> getPermissions(final Authentication authentication, final Class<?> dtoClazz, final Serializable key) {
        return rulesMap.getOrDefault(dtoClazz, emptyPermissions()).keySet().stream().filter(
                permission -> hasPermission(authentication, key, dtoClazz, permission)
        ).sorted().collect(toList());
    }

    private static ListOfOwnerAndMethod emptyMethods() {
        return new ListOfOwnerAndMethod();
    }


    private static PermissionsToPermissionsMethods emptyPermissions() {
        return new PermissionsToPermissionsMethods();
    }


    /**
     * An Alias for a List of owning Objects and a single Method on the owning Object.
     * Thus representing a List of callable functions.
     */
    public static class ListOfOwnerAndMethod extends ArrayList<Pair<Object, Method>> {
        public static ListOfOwnerAndMethod from(List<Pair<Object, Method>> list) {
            ListOfOwnerAndMethod listOfMethods = new ListOfOwnerAndMethod();
            listOfMethods.addAll(list);
            return listOfMethods;
        }
    }

    public static class PermissionsToPermissionsMethods extends HashMap<String, ListOfOwnerAndMethod> {
    }

    public static class DtoClassToPermissionsToPermissionsMethods extends HashMap<Class<?>, PermissionsToPermissionsMethods> {
    }

    public static class DtoClassToPermissionsMethods extends HashMap<Class<?>, ListOfOwnerAndMethod> {
    }

    public static class DtoClassToLookupMethods extends HashMap<Class<?>, ListOfOwnerAndMethod> {
    }

    public static class DtoClassToLookupMethod extends HashMap<Class<?>, Pair<Object, Method>> {
        public static DtoClassToLookupMethod from(Map<Class<?>, Pair<Object, Method>> map) {
            DtoClassToLookupMethod dtoClassToLookupMethod = new DtoClassToLookupMethod();
            dtoClassToLookupMethod.putAll(map);
            return dtoClassToLookupMethod;
        }
    }

}

