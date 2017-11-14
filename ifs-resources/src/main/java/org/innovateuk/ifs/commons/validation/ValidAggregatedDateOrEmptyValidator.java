package org.innovateuk.ifs.commons.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDateOrEmpty;
import org.innovateuk.ifs.util.TimeZoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;


/**
 * A validator that asserts that the aggregation of day, month and year attributes can be combined into a a valid {@link ZonedDateTime}.
 */
public class ValidAggregatedDateOrEmptyValidator implements ConstraintValidator<ValidAggregatedDateOrEmpty, Object> {
    private ValidAggregatedDateOrEmpty validAggregatedDateOrEmpty;

    private static final Log LOG = LogFactory.getLog(ValidAggregatedDateOrEmptyValidator.class);

    @Override
    public void initialize(ValidAggregatedDateOrEmpty validAggregatedDateOrEmpty) {
        this.validAggregatedDateOrEmpty = validAggregatedDateOrEmpty;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Integer yearValue = 0;
        Integer monthValue = 0;
        Integer dayValue = 0;

        try {
            yearValue = (Integer) PropertyUtils.getProperty(object, validAggregatedDateOrEmpty.yearField());
            monthValue = (Integer) PropertyUtils.getProperty(object, validAggregatedDateOrEmpty.monthField());
            dayValue = (Integer) PropertyUtils.getProperty(object, validAggregatedDateOrEmpty.dayField());
        }
        catch(Exception e) {
            LOG.info("Cannot aggregate date properties", e);
            return false;
        }

        if(!dateValuesAllEmpty(yearValue, monthValue, dayValue)) {
            ZonedDateTime localDate;
            try {
                localDate = TimeZoneUtil.fromUkTimeZone(yearValue, monthValue, dayValue);
            }
            catch(Exception e) {
                LOG.info("Cannot create ZonedDateTime from aggregated date properties", e);
                return false;
            }
        }

        return true;
    }

    private boolean dateValuesAllEmpty(Integer yearValue, Integer monthValue, Integer dayValue) {
        return valueIsEmpty(yearValue) && valueIsEmpty(monthValue) && valueIsEmpty(dayValue);
    }

    private boolean valueIsEmpty(Integer yearValue) {
        return yearValue == null || yearValue.equals(0);
    }
}
