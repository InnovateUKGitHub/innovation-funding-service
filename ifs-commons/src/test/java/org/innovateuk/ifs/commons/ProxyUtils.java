package org.innovateuk.ifs.commons;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class ProxyUtils {

    public static List<Object> unwrapProxies(Collection<Object> services) {
        List<Object> unwrappedProxies = new ArrayList<>();
        for (Object service : services) {
            if (AopUtils.isAopProxy(service)) {
                try {
                    unwrappedProxies.add(((Advised) service).getTargetSource().getTarget());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                unwrappedProxies.add(service);
            }
        }
        return unwrappedProxies;
    }

    public static Object unwrapProxy(Object services) {
        try {
            return unwrapProxies(asList(services)).get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
