package com.worth.ifs.validator;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.handler.OrganisationFinanceDelegate;
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

    private OrganisationFinanceDelegate organisationFinanceDelegate;
    private CostRepository costRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GrantClaim.class.equals(clazz);
    }

    @Autowired
    public GrantClaimValidator(OrganisationFinanceDelegate organisationFinanceDelegate, CostRepository costRepository) {
        this.organisationFinanceDelegate = organisationFinanceDelegate;
        this.costRepository = costRepository;
    }

    @Override
    public void validate(Object target, Errors errors) {
        GrantClaim response = (GrantClaim) target;
        Cost cost = costRepository.findOne(response.getId());
        OrganisationSize size = cost.getApplicationFinance().getOrganisationSize();

        if(response.getGrantClaimPercentage() == null || response.getGrantClaimPercentage().equals(0)){
            errors.rejectValue("grantClaimPercentage", "org.hibernate.validator.constraints.NotBlank.message", null, null);
        }else if(response.getGrantClaimPercentage() > size.getMaxGrantClaimPercentage()){
            errors.rejectValue("grantClaimPercentage", "Max", String.format("This field should be %s%% or lower", size.getMaxGrantClaimPercentage()));
        }
    }
}
