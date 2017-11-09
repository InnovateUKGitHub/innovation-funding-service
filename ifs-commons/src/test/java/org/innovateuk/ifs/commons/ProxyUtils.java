package org.innovateuk.ifs.commons;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.util.Collection;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class ProxyUtils {

    public static List<Object> unwrapProxies(Collection<Object> services) {
        return simpleMap(services, ProxyUtils::unwrapProxy);
    }

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
}
