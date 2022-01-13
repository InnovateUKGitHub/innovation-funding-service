package org.innovateuk.ifs.commons.validation;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;


/**
 * A validator that asserts that the aggregation of day, month and year attributes can be combined into a a valid {@link ZonedDateTime}.
 */
@Slf4j
public class ValidAggregatedDateValidator implements ConstraintValidator<ValidAggregatedDate, Object> {
    private ValidAggregatedDate validAggregatedDate;

    @Override
    public void initialize(ValidAggregatedDate validAggregatedDate) {
        this.validAggregatedDate = validAggregatedDate;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Integer yearValue = 0;
        Integer monthValue = 0;
        Integer dayValue = 0;
        boolean required = validAggregatedDate.required();

        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
            yearValue = (Integer) beanWrapper.getPropertyValue(validAggregatedDate.yearField());
            monthValue = (Integer) beanWrapper.getPropertyValue(validAggregatedDate.monthField());
            dayValue = (Integer) beanWrapper.getPropertyValue(validAggregatedDate.dayField());
        }
        catch(Exception e) {
            log.info("Cannot aggregate date properties", e);
            return false;
        }

        if(required || !dateValuesAllEmpty(yearValue, monthValue, dayValue)) {
            try {
                TimeZoneUtil.fromUkTimeZone(yearValue, monthValue, dayValue);
            }
            catch(Exception e) {
                log.info("Cannot create ZonedDateTime from aggregated date properties", e);
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
