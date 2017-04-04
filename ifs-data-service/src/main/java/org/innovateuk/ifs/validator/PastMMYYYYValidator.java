package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;

import static java.time.format.ResolverStyle.STRICT;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is date representation present and that it is in the past
 * Format MM-YYYY
 */
@Component
public class PastMMYYYYValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(PastMMYYYYValidator.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-uuuu").withResolverStyle(STRICT);

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;
        String responseValue = response.getValue();
        if (responseValue == null) {
            rejectValue(errors, "value", "validation.standard.mm.yyyy.format");
        } else {
            try {
                TemporalAccessor date = formatter.parse(responseValue); // This does not throw parse exceptions for invalid months.
                int year = date.get(YEAR); //
                int month = date.get(MONTH_OF_YEAR); // This throws if it has an invalid month.
                TemporalAccessor now = ZonedDateTime.now();
                if (date.get(YEAR) > now.get(YEAR) ||
                        (date.get(YEAR) == now.get(YEAR) && date.get(MONTH_OF_YEAR) > now.get(MONTH_OF_YEAR))) {
                    rejectValue(errors, "value", "validation.standard.past.mm.yyyy.not.past.format");
                }
            }
            catch (DateTimeException e) {
                rejectValue(errors, "value", "validation.standard.mm.yyyy.format");
            }
        }
    }
}
