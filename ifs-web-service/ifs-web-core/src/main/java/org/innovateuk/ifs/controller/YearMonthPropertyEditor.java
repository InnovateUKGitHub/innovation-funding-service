package org.innovateuk.ifs.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.WebRequest;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.util.Map;

/**
 * TODO DW - document
 */
public class YearMonthPropertyEditor extends PropertyEditorSupport {

    private static final Log LOG = LogFactory.getLog(YearMonthPropertyEditor.class);

    private WebRequest webRequest;

    public YearMonthPropertyEditor(WebRequest webRequest) {
        this.webRequest = webRequest;
    }

    @Override
    public void setAsText(String dateFieldName) throws IllegalArgumentException {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();

        Integer year = returnZeroWhenNotValid(parameterMap, dateFieldName + ".yearValue", ChronoField.YEAR, LocalDate.MIN.getYear());
        Integer month = returnZeroWhenNotValid(parameterMap, dateFieldName + ".monthValue", ChronoField.MONTH_OF_YEAR, LocalDate.MIN.getMonthValue());

        try {
            setValue(YearMonth.of(year, month));

        } catch (Exception ex) {
            LOG.error(ex);
            setValue(LocalDate.MIN);
        }
    }

    private Integer returnZeroWhenNotValid(Map<String, String[]> parameterMap, String parameterName, ChronoField chronoField, int defaultValueIfInvalid) {
        try {
            return chronoField.checkValidIntValue(Long.valueOf(parameterMap.get(parameterName)[0]));
        } catch (Exception e){
            LOG.error(e);
            return defaultValueIfInvalid;
        }
    }
}
