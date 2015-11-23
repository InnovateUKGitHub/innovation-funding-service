package com.worth.ifs.security;

import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected ResponseRepository responseRepository;

    private Map<Class<?>, Map<String, List<Pair<Object, Method>>>> rulesMap;


    @PostConstruct
    void generateRules() {
        List<Pair<Object, Method>> allRulesMethods = findRules(applicationContext.getBeansWithAnnotation(PermissionRules.class).values());
        Map<Class<?>, List<Pair<Object, Method>>> collectedRulesMethods = dtoClassToMethods(allRulesMethods);
        // TODO validation stage to check that no one has done anything silly with method signatures?
        rulesMap = dtoClassToPermissionToMethods(collectedRulesMethods);

    }

    List<Pair<Object, Method>> findRules(Collection<Object> permissionRulesBeans) {
        List<Pair<Object, List<Method>>> beansAndPermissionMethods = permissionRulesBeans.stream().
                map(rulesClassInstance -> Pair.of(rulesClassInstance, asList(rulesClassInstance.getClass().getMethods()))).
                map(beanAndAllMethods -> {
                    List<Method> permissionsRuleMethods = beanAndAllMethods.getRight().stream().filter(method -> method.getAnnotationsByType(PermissionRule.class).length > 0).collect(toList());
                    return Pair.of(beanAndAllMethods.getLeft(), permissionsRuleMethods);
                }).collect(toList());

        return beansAndPermissionMethods.stream().flatMap(beanAndPermissionMethods -> {
            Object bean = beanAndPermissionMethods.getLeft();
            return beanAndPermissionMethods.getRight().stream().map(method -> Pair.of(bean, method));
        }).collect(toList());
    }

    Map<Class<?>, List<Pair<Object, Method>>> dtoClassToMethods(List<Pair<Object, Method>> allRuleMethods) {
        // TODO can this be done with java 8 collectors
        Map<Class<?>, List<Pair<Object, Method>>> map = new HashMap<>();
        for (Pair<Object, Method> methodAndBean : allRuleMethods) {
            map.putIfAbsent(methodAndBean.getRight().getParameterTypes()[0], new ArrayList<Pair<Object, Method>>());
            map.get(methodAndBean.getRight().getParameterTypes()[0]).add(methodAndBean);
        }
        return map;
    }


    Map<Class<?>, Map<String, List<Pair<Object, Method>>>> dtoClassToPermissionToMethods(Map<Class<?>, List<Pair<Object, Method>>> dtoClassToMethods) {
        // TODO can this be done with java 8 collectors
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
        String permissionName = permission.toString();
        List<Pair<Object, Method>> methodsToCheck = rulesMap.getOrDefault(dtoClass, emptyMap()).getOrDefault(permission, emptyList());
        return methodsToCheck.stream().map(
                methodAndBean -> callHasPermissionMethod(methodAndBean, targetDomainObject, authentication)
        ).reduce(
                false, (a, b) -> a || b
        );
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new RuntimeException("TODO");
    }

    private boolean callHasPermissionMethod(Pair<Object, Method> methodAndBean, Object dto, Authentication authentication) {
        try {
            final Object finalAuthentication;

            Class<?> secondParameter = methodAndBean.getRight().getParameterTypes()[1];

            if (secondParameter.equals(User.class) && authentication instanceof UserAuthentication) {
                finalAuthentication = ((UserAuthentication) authentication).getDetails();
            } else if (secondParameter.isAssignableFrom(Authentication.class)) {
                finalAuthentication = authentication;
            } else {
                throw new IllegalArgumentException("Second parameter of @PermissionRule-annotated methods should be " +
                        "either a User or an org.springframework.security.core.Authentication implementation");
            }

            return (Boolean) methodAndBean.getRight().invoke(methodAndBean.getLeft(), dto, finalAuthentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

