package org.innovateuk.ifs.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.WebRequest;

import java.beans.PropertyEditorSupport;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.util.Map;

/**
 * A property editor that captures 2 individual form attributes (one for month, one for year) and combines them into a
 * single YearMonth value that is then bound to a field on the form.
 */
@Slf4j
public class YearMonthPropertyEditor extends PropertyEditorSupport {

    private WebRequest webRequest;

    public YearMonthPropertyEditor(WebRequest webRequest) {
        this.webRequest = webRequest;
    }

    @Override
    public void setAsText(String dateFieldName) throws IllegalArgumentException {

        Map<String, String[]> parameterMap = webRequest.getParameterMap();

        int year = returnMinusOneWhenNotValid(parameterMap, dateFieldName + "YearValue", ChronoField.YEAR);
        int month = returnMinusOneWhenNotValid(parameterMap, dateFieldName + "MonthValue", ChronoField.MONTH_OF_YEAR);

        if (year == -1 || month == -1) {
            setValue(null);
        } else {
            try {
                setValue(YearMonth.of(year, month));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                setValue(null);
            }
        }
    }

    private int returnMinusOneWhenNotValid(Map<String, String[]> parameterMap, String parameterName, ChronoField chronoField) {
        try {
            return chronoField.checkValidIntValue(Long.valueOf(parameterMap.get(parameterName)[0]));
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return -1;
        }
    }
}
