package org.innovateuk.ifs.validator;

import jdk.nashorn.internal.runtime.options.Option;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is date representation present and that it is in the past
 * Format MM-YYYY. Note that we are not using a date time formatter due to the difficulties getting it to acknowledge months greater than 12 as being invalid.
 */
@Component
public class PastMMYYYYValidator extends BaseValidator {

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;
        String responseValue = response.getValue();
        if (responseValue == null) {
            rejectValue(errors, "value", "validation.standard.mm.yyyy.format");
        } else {
            try {
                TemporalAccessor now = LocalDateTime.now();
                Pair<Integer, Integer> monthAndYear = monthAndYear(responseValue);
                int month = monthAndYear.getLeft();
                int year = monthAndYear.getRight();
                if (year > now.get(YEAR) ||
                        (year == now.get(YEAR) && month > now.get(MONTH_OF_YEAR))) {
                    rejectValue(errors, "value", "validation.standard.past.mm.yyyy.not.past.format");
                }
            }
            catch (DateTimeException e) {
                rejectValue(errors, "value", "validation.standard.mm.yyyy.format");
            }
        }
    }

    private Pair<Integer, Integer> monthAndYear(String response) throws DateTimeException
    {
        String[] monthAndYear = response.split("-");
        if (monthAndYear.length != 2){
            throw new DateTimeException("Invalid format");
        }
        return Pair.of(month(monthAndYear[0]), year(monthAndYear[1]));
    }

    private Integer year(String yearString){
        try {
            int year = Integer.parseInt(yearString);
            if (year < 0) {
                throw new DateTimeException("Invalid year - must be greater than zero");
            }
            return year;
        } catch (NumberFormatException e){
            throw new DateTimeException("Invalid year - not a number");
        }
    }


    private Integer month(String monthString){
        try {
            int month = Integer.parseInt(monthString);
            if (month < 1 || month > 12){
                throw new DateTimeException("Invalid month - must be between 12 an ");
            }
            else {
                return month;
            }
        } catch (NumberFormatException e){
            throw new DateTimeException("Invalid month - not a number");
        }
    }

}
