package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConditionalMaxLabourDaysValidator  implements ConstraintValidator<ConditionalMaxLabourDays, LabourCost> {
    private static final Log LOG = LogFactory.getLog(ConditionalMaxLabourDaysValidator.class);
    @Override
    public void initialize(final ConditionalMaxLabourDays annotation) {
    }

    @Override
    public boolean isValid(final LabourCost value, final ConstraintValidatorContext ctx) {
        if (value == null) {
            return true;
        }

        if(StringUtils.isNotEmpty(value.getName())
                && value.getName().equals(LabourCostCategory.WORKING_DAYS_KEY)){
            if(value.getLabourDays() != null && value.getLabourDays() > 365){
                // A year can not have more working days then 365...
                return false;
            }else{
                return true;
            }
        }
        return true;
    }
}
