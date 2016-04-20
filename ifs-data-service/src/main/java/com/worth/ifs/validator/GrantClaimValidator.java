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
    @Override
    public boolean supports(Class<?> clazz) {
        return GrantClaim.class.equals(clazz);
    }
    private static final Log LOG = LogFactory.getLog(GrantClaimValidator.class);

    static OrganisationFinanceDelegate organisationFinanceDelegate;
    static CostRepository costRepository;

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

        LOG.info(String.format("Validate grant claim percentage: %s  /  %s", response.getGrantClaimPercentage(), size.getMaxGrantClaimPercentage()));
        if(response.getGrantClaimPercentage() > size.getMaxGrantClaimPercentage()){
            LOG.info("Invalid grant claim percentage.");
            errors.rejectValue("grantClaimPercentage", "Max", String.format("This field should be %s%% or lower", size.getMaxGrantClaimPercentage()));
        }
    }
}
