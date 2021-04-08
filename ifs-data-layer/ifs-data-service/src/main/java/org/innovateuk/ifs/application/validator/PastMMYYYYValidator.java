package org.innovateuk.ifs.application.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static java.time.format.ResolverStyle.STRICT;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is date representation present and that it is in the past
 * Format MM-YYYY
 */
@Component
public class PastMMYYYYValidator extends BaseValidator {
    private static final String VALUE = "value";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-uuuu").withResolverStyle(STRICT);

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;
        String responseValue = response.getValue();
        if (responseValue == null) {
            rejectValue(errors, VALUE, "validation.standard.mm.yyyy.format");
        } else {
            try {
                TemporalAccessor date = formatter.parse(responseValue); // This does not throw parse exceptions for invalid months.
                date.get(MONTH_OF_YEAR); // This throws if it has an invalid month.

                TemporalAccessor now = TimeZoneUtil.toUkTimeZone(ZonedDateTime.now());
                if (date.get(YEAR) > now.get(YEAR) ||
                        (date.get(YEAR) == now.get(YEAR) && date.get(MONTH_OF_YEAR) > now.get(MONTH_OF_YEAR))) {
                    rejectValue(errors, VALUE, "validation.standard.past.mm.yyyy.not.past.format");
                }

                if (date.get(YEAR) < 0) {
                    rejectValue(errors, VALUE, "validation.standard.mm.yyyy.format");
                }
            }
            catch (DateTimeException e) {
                rejectValue(errors, VALUE, "validation.standard.mm.yyyy.format");
            }
        }
    }
}
