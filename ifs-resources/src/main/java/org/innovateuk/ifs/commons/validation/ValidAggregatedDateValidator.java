package org.innovateuk.ifs.commons.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.util.TimeZoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;


/**
 * A validator that asserts that the aggregation of day, month and year attributes can be combined into a a valid {@link ZonedDateTime}.
 */
public class ValidAggregatedDateValidator implements ConstraintValidator<ValidAggregatedDate, Object> {
    private ValidAggregatedDate validAggregatedDate;

    private static final Log LOG = LogFactory.getLog(ValidAggregatedDateValidator.class);

    @Override
    public void initialize(ValidAggregatedDate validAggregatedDate) {
        this.validAggregatedDate = validAggregatedDate;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Integer yearValue = 0;
        Integer monthValue = 0;
        Integer dayValue = 0;

        try {
            yearValue = (Integer) PropertyUtils.getProperty(object, validAggregatedDate.yearField());
            monthValue = (Integer) PropertyUtils.getProperty(object, validAggregatedDate.monthField());
            dayValue = (Integer) PropertyUtils.getProperty(object, validAggregatedDate.dayField());
        }
        catch(Exception e) {
            LOG.info("Cannot aggregate date properties", e);
            return false;
        }

        ZonedDateTime localDate;

        try {
            localDate = TimeZoneUtil.fromUkTimeZone(yearValue, monthValue, dayValue);
        }
        catch(Exception e) {
            LOG.info("Cannot create ZonedDateTime from aggregated date properties", e);
            return false;
        }

        return true;
    }
}
