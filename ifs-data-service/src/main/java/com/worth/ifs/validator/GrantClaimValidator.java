package com.worth.ifs.validator;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.user.domain.OrganisationSize;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * This class validates the GrantClaim.
 */
@Component
public class GrantClaimValidator implements Validator {
    private static final Log LOG = LogFactory.getLog(GrantClaimValidator.class);

    @Autowired
    private CostRepository costRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GrantClaim.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GrantClaim response = (GrantClaim) target;
        Cost cost = costRepository.findOne(response.getId());
        OrganisationSize size = cost.getApplicationFinance().getOrganisationSize();

        if(size == null) {
            errors.rejectValue("grantClaimPercentage", "validation.finance.select.organisation.size");
        } else if(response.getGrantClaimPercentage() == null || response.getGrantClaimPercentage().equals(0)) {
            errors.rejectValue("grantClaimPercentage", "org.hibernate.validator.constraints.NotBlank.message");
        } else if(response.getGrantClaimPercentage() > size.getMaxGrantClaimPercentage()){
            errors.rejectValue("grantClaimPercentage", "Max", String.format("This field should be %s%% or lower", size.getMaxGrantClaimPercentage()));
        } else if(response.getGrantClaimPercentage().intValue() <= 0){
            errors.rejectValue("grantClaimPercentage", "Min", String.format("This field should be %s%% or higher", 1));
        }
    }
}
