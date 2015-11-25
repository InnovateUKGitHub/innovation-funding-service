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

import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Log LOG = LogFactory.getLog(CustomPermissionEvaluator.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<Class<?>, Map<String, List<Pair<Object, Method>>>> rulesMap;
    private Map<Class<?>, Pair<Object, Method>> lookupStrategyMap;

    @PostConstruct
    void generateRules() {

        Collection<Object> permissionRuleBeans = applicationContext.getBeansWithAnnotation(PermissionRules.class).values();
        List<Pair<Object, Method>> allRulesMethods = findRules(permissionRuleBeans);
        Map<Class<?>, List<Pair<Object, Method>>> collectedRulesMethods = dtoClassToMethods(allRulesMethods);
        // TODO RP - validation stage to check that no one has done anything silly with method signatures?
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
        List<Pair<Object, Method>> allLookupStrategyMethods = findLookupStrategies(permissionEntityLookupBeans);
        Map<Class<?>, List<Pair<Object, Method>>> collectedPermissionLookupMethods = returnTypeToMethods(allLookupStrategyMethods);
        // TODO DW - validation stage as per RP's todo above in generateRules()?
        lookupStrategyMap = collectedPermissionLookupMethods.entrySet().stream().
                map(entry -> Pair.of(entry.getKey(), getOnlyElement(entry.getValue()))).
                collect(toMap(Pair::getLeft, Pair::getRight));
    }

    List<Pair<Object, Method>> findRules(Collection<Object> ruleContainingBeans) {
        return findAnnotatedMethods(ruleContainingBeans, PermissionRule.class);
    }

    List<Pair<Object, Method>> findLookupStrategies(Collection<Object> permissionEntityLookupBeans) {
        return findAnnotatedMethods(permissionEntityLookupBeans, PermissionEntityLookupStrategy.class);
    }

    List<Pair<Object, Method>> findAnnotatedMethods(Collection<Object> owningBeans, Class<? extends Annotation> annotation) {
        List<Pair<Object, List<Method>>> beansAndPermissionMethods = owningBeans.stream().
                map(rulesClassInstance -> Pair.of(rulesClassInstance, asList(rulesClassInstance.getClass().getMethods()))).
                map(beanAndAllMethods -> {
                    List<Method> permissionsRuleMethods = beanAndAllMethods.getRight().stream().filter(method -> method.getAnnotationsByType(annotation).length > 0).collect(toList());
                    return Pair.of(beanAndAllMethods.getLeft(), permissionsRuleMethods);
                }).collect(toList());

        return beansAndPermissionMethods.stream().flatMap(beanAndPermissionMethods -> {
            Object bean = beanAndPermissionMethods.getLeft();
            return beanAndPermissionMethods.getRight().stream().map(method -> Pair.of(bean, method));
        }).collect(toList());
    }

    Map<Class<?>, List<Pair<Object, Method>>> dtoClassToMethods(List<Pair<Object, Method>> allRuleMethods) {
        Map<Class<?>, List<Pair<Object, Method>>> map = new HashMap<>();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getParameterTypes()[0], new ArrayList<>());
            map.get(methodAndBean.getRight().getParameterTypes()[0]).add(methodAndBean);
        }
        return map;
    }

    Map<Class<?>, List<Pair<Object, Method>>> returnTypeToMethods(List<Pair<Object, Method>> allRuleMethods) {
        Map<Class<?>, List<Pair<Object, Method>>> map = new HashMap<>();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getReturnType(), new ArrayList<>());
            map.get(methodAndBean.getRight().getReturnType()).add(methodAndBean);
        }
        return map;
    }


    Map<Class<?>, Map<String, List<Pair<Object, Method>>>> dtoClassToPermissionToMethods(Map<Class<?>, List<Pair<Object, Method>>> dtoClassToMethods) {
        Map<Class<?>, Map<String, List<Pair<Object, Method>>>> map = new HashMap<>();
        for (Map.Entry<Class<?>, List<Pair<Object, Method>>> entry : dtoClassToMethods.entrySet()) {
            for (Pair<Object, Method> methodAndBean : entry.getValue()) {
                String permission = methodAndBean.getRight().getAnnotationsByType(PermissionRule.class)[0].value();
                map.putIfAbsent(entry.getKey(), new HashMap<>());
                map.get(entry.getKey()).putIfAbsent(permission, new ArrayList<>());
                map.get(entry.getKey()).get(permission).add(methodAndBean);
            }
        }
        return map;
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetDomainObject, final Object permission) {
        Class<?> dtoClass = targetDomainObject.getClass();
        List<Pair<Object, Method>> methodsToCheck = rulesMap.getOrDefault(dtoClass, emptyMap()).getOrDefault(permission, emptyList());
        return methodsToCheck.stream().map(
                methodAndBean -> callHasPermissionMethod(methodAndBean, targetDomainObject, authentication)
        ).reduce(
                false, (a, b) -> a || b
        );
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, Class<?> targetType, Object permission){
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
        return rulesMap.getOrDefault(targetDomainObject.getClass(), emptyMap()).keySet().stream().filter(
                permission -> hasPermission(authentication, targetDomainObject, permission)
        ).collect(toList());
    }

    public List<String> getPermissions(final Authentication authentication, final Class<?> dtoClazz, final Serializable key) {
        return rulesMap.getOrDefault(dtoClazz, emptyMap()).keySet().stream().filter(
                permission -> hasPermission(authentication,key,dtoClazz, permission)
        ).collect(toList());
    }
}

