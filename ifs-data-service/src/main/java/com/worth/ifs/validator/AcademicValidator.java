package com.worth.ifs.validator;

import com.worth.ifs.finance.resource.cost.AcademicCost;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.worth.ifs.commons.rest.ValidationMessages.reject;

/**
 * This class validates the AcademicCost instances.
 */
@Component
public class AcademicValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return AcademicCost.class.equals(clazz);
    }
    private static final Log LOG = LogFactory.getLog(AcademicValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        AcademicCost response = (AcademicCost) target;

        if("tsb_reference".equals(response.getName()) && StringUtils.isBlank(response.getItem())){
            reject(errors, "This field cannot be left blank");
        }
    }
}
