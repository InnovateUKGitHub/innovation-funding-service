package com.worth.ifs.commons.util.enums;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * An enum with identity.
 */
public interface Identifiable {

    long getId();

    static <T extends Identifiable> Map<Long, T> toIdMap(T [] values) {
        return stream(values).collect(toMap(Identifiable::getId, identity()));
    }
}
