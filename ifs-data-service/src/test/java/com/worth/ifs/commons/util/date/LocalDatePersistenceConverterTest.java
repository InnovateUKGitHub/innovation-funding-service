package com.worth.ifs.commons.util.date;

import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class LocalDatePersistenceConverterTest {

    @Test
    public void test_convertToDatabaseColumn() {

        LocalDate originalDate = LocalDate.of(2015, 3, 5);
        Date convertedDate = new LocalDatePersistenceConverter().convertToDatabaseColumn(originalDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(convertedDate.getTime());

        assertEquals(5, cal.get(Calendar.DATE));
        assertEquals(2, cal.get(Calendar.MONTH));
        assertEquals(2015, cal.get(Calendar.YEAR));
    }

    @Test
    public void test_convertToDatabaseColumn_nullSafe() {

        LocalDate originalDate = null;
        Date convertedDate = new LocalDatePersistenceConverter().convertToDatabaseColumn(originalDate);
        assertNull(convertedDate);
    }

    @Test
    public void test_convertToEntityAttribute() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 5);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.YEAR, 2015);

        Date originalDate = new Date(cal.getTimeInMillis());
        LocalDate convertedDate = new LocalDatePersistenceConverter().convertToEntityAttribute(originalDate);

        assertEquals(5, convertedDate.getDayOfMonth());
        assertEquals(3, convertedDate.getMonthValue());
        assertEquals(2015, convertedDate.getYear());
    }

    @Test
    public void test_convertToEntityAttribute_nullSafe() {

        LocalDate originalDate = null;
        Date convertedDate = new LocalDatePersistenceConverter().convertToDatabaseColumn(originalDate);
        assertNull(convertedDate);
    }
}
