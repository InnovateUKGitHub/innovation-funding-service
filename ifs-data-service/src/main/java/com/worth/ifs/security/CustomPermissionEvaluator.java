package com.worth.ifs.security;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
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


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Log LOG = LogFactory.getLog(CustomPermissionEvaluator.class);

    @Autowired
    private ApplicationContext applicationContext;

    private DtoClassToPermissionsToPermissionsMethods rulesMap;


    private DtoClassToLookupMethod lookupStrategyMap;

    @PostConstruct
    void generateRules() {
        Collection<Object> permissionRuleBeans = applicationContext.getBeansWithAnnotation(PermissionRules.class).values();
        ListOfMethods allRulesMethods = findRules(permissionRuleBeans);

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
                PermissionRule permissionAnnotation = permissionMethod.getAnnotation(PermissionRule.class);
                LOG.debug("Registered PermissionRule: " + permissionAnnotation.description());
            })));
        }
    }

    @PostConstruct
    void generateLookupStrategies() {
        Collection<Object> permissionEntityLookupBeans = applicationContext.getBeansWithAnnotation(PermissionEntityLookupStrategies.class).values();
        ListOfMethods allLookupStrategyMethods = findLookupStrategies(permissionEntityLookupBeans);

        List<Pair<Object, Method>> failed = failedLookupMethodSignatures(allLookupStrategyMethods);
        if (!failed.isEmpty()) {
            String error = "Lookup methods: " + Arrays.toString(failed.toArray()) + " have an incorrect signature";
            LOG.error(error);
            throw new IllegalStateException(error); // Fail fast
        }

        DtoClassToLookupMethods collectedPermissionLookupMethods = returnTypeToMethods(allLookupStrategyMethods);
        lookupStrategyMap = DtoClassToLookupMethod.from(collectedPermissionLookupMethods.entrySet().stream().
                map(entry -> Pair.of(entry.getKey(), getOnlyElement(entry.getValue()))).
                collect(toMap(Pair::getLeft, Pair::getRight)));
    }

    List<Pair<Object, Method>> failedPermissionMethodSignatures(ListOfMethods collectedRulesMethods) {
        return collectedRulesMethods.stream().filter(
                beanAndMethod -> {
                    Method method = beanAndMethod.getRight();
                    if (method.getParameterTypes().length == 2) {
                        Class<?> secondParameterClass = method.getParameterTypes()[1];
                        if (Authentication.class.isAssignableFrom(secondParameterClass) ||
                                User.class.isAssignableFrom(secondParameterClass)) {
                            return false;
                        }
                    }
                    return true;
                }
        ).collect(toList());
    }

    List<Pair<Object, Method>> failedLookupMethodSignatures(ListOfMethods collectedLookupMethods) {
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


    ListOfMethods findRules(Collection<Object> ruleContainingBeans) {
        return findAnnotatedMethods(ruleContainingBeans, PermissionRule.class);
    }

    ListOfMethods findLookupStrategies(Collection<Object> permissionEntityLookupBeans) {
        return findAnnotatedMethods(permissionEntityLookupBeans, PermissionEntityLookupStrategy.class);
    }

    ListOfMethods findAnnotatedMethods(Collection<Object> owningBeans, Class<? extends Annotation> annotation) {
        List<Pair<Object, List<Method>>> beansAndPermissionMethods = owningBeans.stream().
                map(rulesClassInstance -> Pair.of(rulesClassInstance, asList(rulesClassInstance.getClass().getMethods()))).
                map(beanAndAllMethods -> {
                    List<Method> permissionsRuleMethods = beanAndAllMethods.getRight().stream().filter(method -> method.getAnnotationsByType(annotation).length > 0).collect(toList());
                    return Pair.of(beanAndAllMethods.getLeft(), permissionsRuleMethods);
                }).collect(toList());
        return ListOfMethods.from(beansAndPermissionMethods.stream().flatMap(beanAndPermissionMethods -> {
            Object bean = beanAndPermissionMethods.getLeft();
            return beanAndPermissionMethods.getRight().stream().map(method -> Pair.of(bean, method));
        }).collect(toList()));

    }

    DtoClassToPermissionsMethods dtoClassToMethods(List<Pair<Object, Method>> allRuleMethods) {
        DtoClassToPermissionsMethods map = new DtoClassToPermissionsMethods();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getParameterTypes()[0], new ListOfMethods());
            map.get(methodAndBean.getRight().getParameterTypes()[0]).add(methodAndBean);
        }
        return map;
    }

    DtoClassToLookupMethods returnTypeToMethods(ListOfMethods allRuleMethods) {
        DtoClassToLookupMethods map = new DtoClassToLookupMethods();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getReturnType(), new ListOfMethods());
            map.get(methodAndBean.getRight().getReturnType()).add(methodAndBean);
        }
        return map;
    }


    DtoClassToPermissionsToPermissionsMethods dtoClassToPermissionToMethods(DtoClassToPermissionsMethods dtoClassToMethods) {
        DtoClassToPermissionsToPermissionsMethods map = new DtoClassToPermissionsToPermissionsMethods();
        for (Entry<Class<?>, ListOfMethods> entry : dtoClassToMethods.entrySet()) {
            for (Pair<Object, Method> methodAndBean : entry.getValue()) {
                String permission = methodAndBean.getRight().getAnnotationsByType(PermissionRule.class)[0].value();
                map.putIfAbsent(entry.getKey(), new PermissionsToPermissionsMethods());
                map.get(entry.getKey()).putIfAbsent(permission, new ListOfMethods());
                map.get(entry.getKey()).get(permission).add(methodAndBean);
            }
        }
        return map;
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetDomainObject, final Object permission) {
        Class<?> dtoClass = targetDomainObject.getClass();
        ListOfMethods methodsToCheck =
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
            throw new IllegalArgumentException("Could not successfully call permission entity lookup method", e);
        }

        if (permissionEntity == null) {
            throw new IllegalArgumentException("Could not find entity of type " + targetType + " with id " + targetId);
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

        Class<?> secondParameter = methodAndBean.getRight().getParameterTypes()[1];

        if (secondParameter.equals(User.class) && authentication instanceof UserAuthentication) {
            finalAuthentication = ((UserAuthentication) authentication).getDetails();
        } else if (Authentication.class.isAssignableFrom(secondParameter)) {
            finalAuthentication = authentication;
        } else {
            throw new IllegalArgumentException("Second parameter of @PermissionRule-annotated methods should be " +
                    "either a User or an org.springframework.security.core.Authentication implementation");
        }

        try {
            return (Boolean) methodAndBean.getRight().invoke(methodAndBean.getLeft(), dto, finalAuthentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getPermissions(Authentication authentication, Object targetDomainObject) {
        return rulesMap.getOrDefault(targetDomainObject.getClass(), emptyPermissions()).keySet().stream().filter(
                permission -> hasPermission(authentication, targetDomainObject, permission)
        ).collect(toList());
    }

    public List<String> getPermissions(final Authentication authentication, final Class<?> dtoClazz, final Serializable key) {
        return rulesMap.getOrDefault(dtoClazz, emptyPermissions()).keySet().stream().filter(
                permission -> hasPermission(authentication, key, dtoClazz, permission)
        ).collect(toList());
    }

    private static ListOfMethods emptyMethods() {
        return new ListOfMethods();
    }


    private static PermissionsToPermissionsMethods emptyPermissions() {
        return new PermissionsToPermissionsMethods();
    }


    public static class ListOfMethods extends ArrayList<Pair<Object, Method>> {
        public static ListOfMethods from(List<Pair<Object, Method>> list) {
            ListOfMethods listOfMethods = new ListOfMethods();
            listOfMethods.addAll(list);
            return listOfMethods;
        }
    }

    ;

    public static class PermissionsToPermissionsMethods extends HashMap<String, ListOfMethods> {
    }

    ;

    public static class DtoClassToPermissionsToPermissionsMethods extends HashMap<Class<?>, PermissionsToPermissionsMethods> {
    }

    ;

    public static class DtoClassToPermissionsMethods extends HashMap<Class<?>, ListOfMethods> {
    }

    ;

    public static class DtoClassToLookupMethods extends HashMap<Class<?>, ListOfMethods> {
    }

    ;

    public static class DtoClassToLookupMethod extends HashMap<Class<?>, Pair<Object, Method>> {
        public static DtoClassToLookupMethod from(Map<Class<?>, Pair<Object, Method>> map) {
            DtoClassToLookupMethod dtoClassToLookupMethod = new DtoClassToLookupMethod();
            dtoClassToLookupMethod.putAll(map);
            return dtoClassToLookupMethod;
        }

    }

    ;


}

