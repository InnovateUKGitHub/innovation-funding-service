package org.innovateuk.ifs.commons;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.SecuredBySpring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.ProxyUtils.classLevel;
import static org.innovateuk.ifs.commons.ProxyUtils.methodLevel;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;

/**
 * Class to represent the {@link SecuredBySpring} {@link Annotation}s on a given Spring bean.
 */
public class SecuredBySpringBeanResult {

    /**
     * The {@link Class} inspected, with the class level {@link SecuredBySpring} if present
     */
    Pair<Class<?>, Optional<SecuredBySpring>> classLevel;

    /**
     * All of the {@link Method}s on the inspected {@link Class}, which are not defined on {@link Object}, with the
     * associated method level {@link SecuredBySpring} if present
     */
    Map<Method, Optional<SecuredBySpring>> methodLevel;

    private SecuredBySpringBeanResult(Pair<Class<?>, Optional<SecuredBySpring>> classLevel, Map<Method, Optional<SecuredBySpring>> methodLevel) {
        this.classLevel = classLevel;
        this.methodLevel = methodLevel;
    }

    public static SecuredBySpringBeanResult fromBean(Object bean){
        Pair<Class<?>, Optional<SecuredBySpring>> classLevelResult = classLevelSecuredBySpring(bean);
        Map<Method, Optional<SecuredBySpring>> methodLevelResult = methodLevelSecuredBySpring(bean);
        return new SecuredBySpringBeanResult(classLevelResult, methodLevelResult);
    }

    private static final Pair<Class<?>, Optional<SecuredBySpring>> classLevelSecuredBySpring(Object bean){
        return classLevel(bean, SecuredBySpring.class);
    }

    private static final Map<Method, Optional<SecuredBySpring>> methodLevelSecuredBySpring(Object bean){
        Map<Method, Optional<SecuredBySpring>> all = methodLevel(bean, SecuredBySpring.class);
        // We do not want Object level methods
        return simpleFilterNot(all, ProxyUtils::isAMethodOnObject);
    }


}
