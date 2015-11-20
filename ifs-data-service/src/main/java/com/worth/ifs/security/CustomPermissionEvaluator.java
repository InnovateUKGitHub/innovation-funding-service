package com.worth.ifs.security;

import com.worth.ifs.application.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;


@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected ResponseRepository responseRepository;

    private Map<Class<?>, Map<String, List<Method>>> rulesMap;


    @PostConstruct
    private void generateRules() {
        List<Method> allRulesMethods = findRules(applicationContext.getBeansWithAnnotation(PermissionRules.class).values());
        Map<Class<?>, List<Method>> collectedRulesMethods = dtoClassToMethods(allRulesMethods);
        // TODO validation stage to check that no one has done anything silly with method signatures?
        rulesMap = dtoClassToPermissionToMethods(collectedRulesMethods);

    }

    List<Method> findRules(Collection<Object> permissionRulesBeans) {
        return permissionRulesBeans.stream()
                .map(
                        rulesClassInstance -> Arrays.asList(rulesClassInstance.getClass().getMethods())
                ).flatMap(
                        methods -> methods.stream()
                ).filter(
                        method -> method.getAnnotationsByType(PermissionRule.class).length > 0
                ).collect(toList());
    }

    Map<Class<?>, List<Method>> dtoClassToMethods(List<Method> allRuleMethods) {
        // TODO can this be done with java 8 collectors
        Map<Class<?>, List<Method>> map = new HashMap<>();
        for (Method method : allRuleMethods) {
            map.putIfAbsent(method.getParameterTypes()[0], new ArrayList<Method>());
            map.get(method.getParameterTypes()[0]).add(method);
        }
        return map;
    }


    Map<Class<?>, Map<String, List<Method>>> dtoClassToPermissionToMethods(Map<Class<?>, List<Method>> dtoClassToMethods) {
        // TODO can this be done with java 8 collectors
        Map<Class<?>, Map<String, List<Method>>> map = new HashMap<>();
        for (Map.Entry<Class<?>, List<Method>> entry : dtoClassToMethods.entrySet()) {
            for (Method method : entry.getValue()) {
                String permission = method.getAnnotationsByType(PermissionRule.class)[0].value();
                map.putIfAbsent(entry.getKey(), new HashMap<>());
                map.get(entry.getKey()).putIfAbsent(permission, new ArrayList<>());
                map.get(entry.getKey()).get(permission).add(method);
            }
        }
        return map;
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object targetDomainObject, final Object permission) {
        Class<?> dtoClass = targetDomainObject.getClass();
        String permissionName = permission.toString();
        List<Method> methodsToCheck = rulesMap.getOrDefault(dtoClass, emptyMap()).getOrDefault(permission, emptyList());
        return methodsToCheck.stream().map(
                method -> callHasPermissionMethod(method, targetDomainObject, authentication)
        ).reduce(
                false, (a, b) -> a || b
        );
    }

    private boolean callHasPermissionMethod(Method method, Object dto, Authentication authentication) {
        try {
            return (Boolean) method.invoke(dto, authentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new RuntimeException("TODO");
    }

}

