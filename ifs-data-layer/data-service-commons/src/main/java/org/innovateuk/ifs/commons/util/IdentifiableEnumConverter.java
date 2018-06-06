package org.innovateuk.ifs.commons.util;


import org.innovateuk.ifs.identity.Identifiable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Generic JPA {@link Converter} for {@link Identifiable} enums.
 *
 * Subclasses must:
 * <ul>
 *   <li>Have the @{@link Converter} annotation</li>
 *   <li>Provide a public default constructor</li>
 * </ul>
 */
public abstract class IdentifiableEnumConverter<T extends Enum<T> & Identifiable> implements AttributeConverter<T, Long> {

    private final Map<Long, T> idMap;

    protected IdentifiableEnumConverter(final Class<T> theEnum) {
        // we can't use a method reference here due to an apparent JVM bug
        idMap = stream(theEnum.getEnumConstants()).collect(toMap(o -> o.getId(), identity()));
    }

    @Override
    public Long convertToDatabaseColumn(final T attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    @Override
    public T convertToEntityAttribute(final Long dbData) {
        return idMap.get(dbData);
    }
}