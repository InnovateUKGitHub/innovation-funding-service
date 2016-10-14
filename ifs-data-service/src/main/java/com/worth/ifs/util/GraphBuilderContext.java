package com.worth.ifs.util;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class to build a graph of objects from a graph of objects
 */
public class GraphBuilderContext {

    private Map<Object, Object> refs = new HashMap<>();

    public <T> T resource(Object domain, Supplier<T> constructor, Consumer<T> populator) {
        if (domain == null) {
            return null;
        }
        if (!refs.containsKey(domain)) {
            T resource = constructor.get();
            refs.put(domain, resource);
            populator.accept(resource);
        }
        return (T) refs.get(domain);
    }
}
