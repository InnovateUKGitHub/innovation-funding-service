package com.worth.ifs.commons.security;

import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
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

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Log LOG = LogFactory.getLog(CustomPermissionEvaluator.class);

    private static final UserResource ANONYMOUS_USER = new UserResource();

    @Autowired
    private ApplicationContext applicationContext;

    private PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap;

    private PermissionedObjectClassesToListOfLookup lookupStrategyMap;

    public static boolean isAnonymous(UserResource user) {
        return user == ANONYMOUS_USER;
    }

    public static UserResource getAnonymous() {
        return ANONYMOUS_USER;
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

        PermissionedObjectClassToPermissionsMethods collectedRulesMethods = dtoClassToMethods(allRulesMethods);
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
        final Collection<Object> permissionEntityLookupBeans = applicationContext.getBeansWithAnnotation(PermissionEntityLookupStrategies.class).values();
        final ListOfOwnerAndMethod allLookupStrategyMethods = findLookupStrategies(permissionEntityLookupBeans);
        final PermissionedObjectClassToLookupMethods collectedPermissionLookupMethods = returnTypeToMethods(allLookupStrategyMethods);
        lookupStrategyMap = PermissionedObjectClassesToListOfLookup.from(collectedPermissionLookupMethods);
        validate(lookupStrategyMap); // Fail Fast
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


    private static final void validate(final PermissionedObjectClassesToListOfLookup lookupsStrategyMap) {
        for (final Entry<Class<?>, ListOfOwnerAndMethod> permissionedObjectClassTolookupsStrategies : lookupsStrategyMap.entrySet()) {
            if (!lookupsWithMethodsThatDoNotHaveASingleParameter(permissionedObjectClassTolookupsStrategies.getValue()).isEmpty()) {
                final String error = "Lookups must have a single parameter";
                LOG.error(error);
                throw new IllegalArgumentException(error);
            }
            if (!lookupsWithMethodsThatDoNotHaveASerializableParameter(permissionedObjectClassTolookupsStrategies.getValue()).isEmpty()) {
                final String error = "Lookups must have a serializable parameter";
                LOG.error(error);
                throw new IllegalArgumentException(error);
            }
            if (!lookupsWithMethodsThatHaveDuplicateLookupKeyParameter(permissionedObjectClassTolookupsStrategies.getValue()).isEmpty()) {
                final String error = "There must not be any duplicates";
                LOG.error(error);
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

    PermissionedObjectClassToPermissionsMethods dtoClassToMethods(List<Pair<Object, Method>> allRuleMethods) {
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


    PermissionedObjectClassToPermissionsToPermissionsMethods dtoClassToPermissionToMethods(PermissionedObjectClassToPermissionsMethods dtoClassToMethods) {
        PermissionedObjectClassToPermissionsToPermissionsMethods map = new PermissionedObjectClassToPermissionsToPermissionsMethods();
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
    public boolean hasPermission(final Authentication authentication, final Object targetObject, final Object permission) {
        if (targetObject == null) {
            return true;
        }
        final Class<?> targetClass = targetObject.getClass();
        final List<PermissionsToPermissionsMethods> permissionsWithPermissionsMethodsForTargetClassList
                = rulesMap.entrySet().stream().
                filter(e -> e.getKey().isAssignableFrom(targetClass)). // Any super class of the target class will do.
                map(Entry::getValue).collect(toList());
        final List<ListOfOwnerAndMethod> permissionMethodsForPermissionList
                = permissionsWithPermissionsMethodsForTargetClassList.stream().
                map(permissionsToPermissionsMethods -> permissionsToPermissionsMethods.get(permission)).
                filter(Objects::nonNull). // Filter any nulls
                collect(toList());
        final ListOfOwnerAndMethod permissionMethodsForPermissionAggregate
                = permissionMethodsForPermissionList.stream().
                reduce(new ListOfOwnerAndMethod(), (f1, f2) -> ListOfOwnerAndMethod.from(combineLists(f1, f2)));
        return permissionMethodsForPermissionAggregate.stream().
                map(methodAndBean -> callHasPermissionMethod(methodAndBean, targetObject, authentication)).
                reduce(false, (a, b) -> a || b);
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, Class<?> targetType, Object permission) {
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
    }

    private Pair<Object, Method> lookup(final Serializable targetId, final Class<?> targetType) {
        final List<Pair<Object, Method>> allLookupsForTargetClass = lookupStrategyMap.get(targetType);
        if (allLookupsForTargetClass == null) {
            final String error = "No lookups at all found for target class " + targetType + " with target id: " + targetId;
            LOG.error(error);
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
            LOG.error(error);
            throw new IllegalArgumentException(error);
        } else if (lookupsForTargetClassAndTargetIdClass.size() > 1) {
            final String error = "Multiple lookups found for target class " + targetType + " with target id: " + targetId;
            LOG.error(error);
            throw new IllegalArgumentException(error);
        }
        return lookupsForTargetClassAndTargetIdClass.get(0);
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

    public static class PermissionedObjectClassToPermissionsToPermissionsMethods extends HashMap<Class<?>, PermissionsToPermissionsMethods> {
    }

    public static class PermissionedObjectClassToPermissionsMethods extends HashMap<Class<?>, ListOfOwnerAndMethod> {
    }

    public static class PermissionedObjectClassToLookupMethods extends HashMap<Class<?>, ListOfOwnerAndMethod> {
    }

    public static class PermissionedObjectClassesToListOfLookup extends HashMap<Class<?>, ListOfOwnerAndMethod> {
        public static PermissionedObjectClassesToListOfLookup from(Map<Class<?>, ListOfOwnerAndMethod> map) {
            final PermissionedObjectClassesToListOfLookup dtoClassToLookupMethod = new PermissionedObjectClassesToListOfLookup();
            dtoClassToLookupMethod.putAll(map);
            return dtoClassToLookupMethod;
        }
    }

}

