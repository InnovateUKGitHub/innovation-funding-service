package com.worth.ifs.controller;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class LocalDatePropertyEditorTest {

    @Test
    public void testSetAsText() {

        MockHttpServletRequest request = createMockRequestWithDate("2017", "7", "2");

        LocalDatePropertyEditor editor = new LocalDatePropertyEditor(new DispatcherServletWebRequest(request));
        editor.setAsText("myField");
        assertEquals("2017-07-02", editor.getAsText());
    }

    @Test
    public void testSetAsTextWithInvalidValuesForMonthDefaultsToOne() {

        MockHttpServletRequest request = createMockRequestWithDate("2016", "NaN", "3");

        LocalDatePropertyEditor editor = new LocalDatePropertyEditor(new DispatcherServletWebRequest(request));
        editor.setAsText("myField");
        assertEquals("2016-01-03", editor.getAsText());
    }

    @Test
    public void testSetAsTextWithInvalidValuesForDayDefaultsToOne() {

        MockHttpServletRequest request = createMockRequestWithDate("2016", "3", "hello");

        LocalDatePropertyEditor editor = new LocalDatePropertyEditor(new DispatcherServletWebRequest(request));
        editor.setAsText("myField");
        assertEquals("2016-03-01", editor.getAsText());
    }

    @Test
    public void testSetAsTextWithInvalidValuesForDateDefaultsToOne() {

        MockHttpServletRequest request = createMockRequestWithDate("2016", "3", "35");

        LocalDatePropertyEditor editor = new LocalDatePropertyEditor(new DispatcherServletWebRequest(request));
        editor.setAsText("myField");
        assertEquals("2016-03-01", editor.getAsText());
    }

    @Test
    public void testSetAsTextWithInvalidValuesForYearDefaultsToThisYear() {

        int thisYear = LocalDate.now().getYear();

        MockHttpServletRequest request = createMockRequestWithDate("hello", "3", "5");

        LocalDatePropertyEditor editor = new LocalDatePropertyEditor(new DispatcherServletWebRequest(request));
        editor.setAsText("myField");
        assertEquals(thisYear + "-03-05", editor.getAsText());
    }

    private MockHttpServletRequest createMockRequestWithDate(String year, String month, String day) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("myField.year", year);
        request.setParameter("myField.monthValue", month);
        request.setParameter("myField.dayOfMonth", day);
        return request;
    }
}
