package com.worth.ifs.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.WebRequest;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Map;

/**
 * This class is used to convert our custom implementation of the date fields, to the LocalDate object.
 * This way, the submitted fields, can be cast / injected into a domain object.
 * We use this to save form fields, into the ApplicationForm object, for example properties of the domain.Application object.
 *
 * One other way to remove this class, would be to merge the day/month/year value into 1 form-input element.
 */
public class LocalDatePropertyEditor extends PropertyEditorSupport {
    private final Log log = LogFactory.getLog(getClass());
    private WebRequest webRequest;

    public LocalDatePropertyEditor(WebRequest webRequest) {
        this.webRequest = webRequest;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();

        // should validate these...
        Integer year = Integer.valueOf(parameterMap.get("application.startDate.year")[0]);
        Integer month = Integer.valueOf(parameterMap.get("application.startDate.monthValue")[0]);
        Integer day = Integer.valueOf(parameterMap.get("application.startDate.dayOfMonth")[0]);

        try {
            setValue(LocalDate.of(year, month, day));
        } catch (Exception ex) {
            setValue(null);
        }
    }
}