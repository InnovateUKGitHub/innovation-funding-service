package com.worth.ifs.validator;

import com.worth.ifs.finance.domain.FinanceRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.worth.ifs.finance.repository.FinanceRowRepository;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;

/**
 * This class validates the GrantClaim.
 */
@Component
public class GrantClaimValidator implements Validator {

    @Autowired
    private FinanceRowRepository financeRowRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GrantClaim.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GrantClaim response = (GrantClaim) target;
        FinanceRow cost = financeRowRepository.findOne(response.getId());

        OrganisationType organisationType = cost.getApplicationFinance().getOrganisation().getOrganisationType();
        
        int max;
        
        if(isAcademicOrBusiness(organisationType)) {
        	
            OrganisationSize size = cost.getApplicationFinance().getOrganisationSize();

        	if(size == null) {
                errors.rejectValue("grantClaimPercentage", "validation.finance.select.organisation.size");
                return;
            }
        	
        	max = size.getMaxGrantClaimPercentage();
        } else {
        	max = 100;
        }
        
    	if(response.getGrantClaimPercentage() == null) {
            errors.rejectValue("grantClaimPercentage", "org.hibernate.validator.constraints.NotBlank.message");
        } else if(response.getGrantClaimPercentage() > max){
            errors.rejectValue("grantClaimPercentage", "Max", String.format("This field should be %s%% or lower", max));
        } else if(response.getGrantClaimPercentage().intValue() < 0){
            errors.rejectValue("grantClaimPercentage", "Min", String.format("This field should be %s%% or higher", 0));
        }
    }
    
    private boolean isAcademicOrBusiness(OrganisationType type) {
    	return OrganisationTypeEnum.ACADEMIC.getOrganisationTypeId().equals(type.getId()) || OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(type.getId());
    }
}
