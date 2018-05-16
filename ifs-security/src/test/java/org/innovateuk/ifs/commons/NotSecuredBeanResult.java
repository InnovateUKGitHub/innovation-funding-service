package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.NotSecured;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.ProxyUtils.methodLevel;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Class to represent the {@link NotSecured} {@link Annotation}s on a given Spring beans methods.
 */
public class NotSecuredBeanResult {

    Map<Method, NotSecured> methodLevel;

    private NotSecuredBeanResult(Map<Method, NotSecured> methodLevel){
        this.methodLevel = methodLevel;
    }

    public static NotSecuredBeanResult fromBean(Object bean){
        Map<Method, NotSecured> methodLevelResult = methodLevelSecuredBySpring(bean);
        return new NotSecuredBeanResult(methodLevelResult);
    }

    private static final Map<Method, NotSecured> methodLevelSecuredBySpring(Object bean){
        Map<Method, Optional<NotSecured>> all = methodLevel(bean, NotSecured.class);
        // Filter out the empties
        Map<Method, Optional<NotSecured>> filtered  = simpleFilter(all, (k,v) -> v.isPresent());
        return filtered.entrySet().stream().collect(toMap(Entry::getKey, e -> e.getValue().get()));
    }


}
