package com.worth.ifs.commons.util.date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * This class converts the new {@link java.time.LocalDateTime} type to a object that can be stored in the database.
 *
 * https://weblogs.java.net/blog/montanajava/archive/2014/06/17/using-java-8-datetime-classes-jpa
 */
@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter implements AttributeConverter<java.time.LocalDateTime, java.sql.Timestamp> {

    @Override
    public java.sql.Timestamp convertToDatabaseColumn(java.time.LocalDateTime attribute) {
        return attribute == null ? null : java.sql.Timestamp.valueOf(attribute);
    }

    @Override
    public java.time.LocalDateTime convertToEntityAttribute(java.sql.Timestamp dbData) {
        return dbData == null ? null : dbData.toLocalDateTime();
    }
}