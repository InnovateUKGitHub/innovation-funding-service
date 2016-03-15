package com.worth.ifs.commons.util.date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


/**
 * This class converts the new {@link java.time.LocalDate} type to a object that can be stored in the database.
 *
 * https://weblogs.java.net/blog/montanajava/archive/2014/06/17/using-java-8-datetime-classes-jpa
 */
@Converter(autoApply = true)
public class LocalDatePersistenceConverter implements AttributeConverter<java.time.LocalDate, java.sql.Date> {

    @Override
    public java.sql.Date convertToDatabaseColumn(java.time.LocalDate attribute) {
        return attribute == null ? null : java.sql.Date.valueOf(attribute);
    }

    @Override
    public java.time.LocalDate convertToEntityAttribute(java.sql.Date dbData) {
        return dbData == null ? null : dbData.toLocalDate();
    }
}