package com.worth.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
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
    private CostRepository costRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GrantClaim.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GrantClaim response = (GrantClaim) target;
        Cost cost = costRepository.findOne(response.getId());

        OrganisationType organisationType = cost.getApplicationFinance().getOrganisation().getOrganisationType();
        
        if(isAcademicOrBusiness(organisationType)) {
        	
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
        } else {
        	if(response.getGrantClaimPercentage() == null) {
                errors.rejectValue("grantClaimPercentage", "org.hibernate.validator.constraints.NotBlank.message");
            } else if(response.getGrantClaimPercentage() > 100){
                errors.rejectValue("grantClaimPercentage", "Max", String.format("This field should be %s%% or lower", 100));
            } else if(response.getGrantClaimPercentage().intValue() < 0){
                errors.rejectValue("grantClaimPercentage", "Min", String.format("This field should be %s%% or higher", 1));
            }
        }
    }
    
    private boolean isAcademicOrBusiness(OrganisationType type) {
    	return OrganisationTypeEnum.ACADEMIC.getOrganisationTypeId().equals(type.getId()) || OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(type.getId());
    }
}
