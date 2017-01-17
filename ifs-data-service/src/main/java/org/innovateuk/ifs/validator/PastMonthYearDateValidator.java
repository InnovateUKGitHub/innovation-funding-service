package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

import static java.math.BigDecimal.ZERO;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is date representation present and that it is in the past
 * Format MM-YYYY
 */
@Component
public class PastMonthYearDateValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(PastMonthYearDateValidator.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;
        String responseValue = response.getValue();
        if (responseValue == null) {
            rejectValue(errors, "value", "validation.standard.mm.yyyy.format");
        } else {
            try {
                TemporalAccessor date = formatter.parse(responseValue);
                TemporalAccessor now = LocalDateTime.now();
                if (date.get(YEAR) > now.get(YEAR) ||
                        (date.get(YEAR) == now.get(YEAR) && date.get(MONTH_OF_YEAR) > now.get(MONTH_OF_YEAR))) {
                    rejectValue(errors, "value", "validation.standard.past.mm.yyyy.not.past.format");
                }
            }
            catch (DateTimeParseException e) {
                rejectValue(errors, "value", "validation.standard.mm.yyyy.format");
            }
        }
    }
}
