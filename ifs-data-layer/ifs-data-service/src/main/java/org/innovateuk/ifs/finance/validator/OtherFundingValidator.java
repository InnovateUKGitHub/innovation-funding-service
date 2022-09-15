package org.innovateuk.ifs.finance.validator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;

/**
 * This class validates the financial 'other funding' inputs
 */
@Component
public class OtherFundingValidator implements Validator {

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;
    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(BaseOtherFunding.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BaseOtherFunding otherFunding = (BaseOtherFunding) target;

        String otherFundingSelection = otherFunding.getOtherPublicFunding();
        validateOtherPublicFunding(otherFundingSelection, errors);

        boolean userHasSelectedYesToOtherFunding = userHasSelectedYes(otherFunding);
        String fundingSource = otherFunding.getFundingSource();
        BigDecimal fundingAmount = otherFunding.getFundingAmount();
        if (userHasSelectedYesToOtherFunding && fundingSource != null && !fundingSource.equals(OTHER_FUNDING)) {
            validateDate(otherFunding, errors, isCompTypeOfgemAndFundingTypeThirdParty(otherFunding));
            validateFundingSource(fundingSource, errors);
            validateFundingAmount(fundingAmount, errors);
        } else if(userHasSelectedYesToOtherFunding && fundingSource == null) {
        	validateDate(otherFunding, errors, isCompTypeOfgemAndFundingTypeThirdParty(otherFunding));
        }
    }

    private void validateOtherPublicFunding(String otherPublicFunding, Errors errors) {
        List<String> allowedStrings = asList(null, "", "Yes", "No");
        if (!allowedStrings.contains(otherPublicFunding)) {
            rejectValue(errors, "otherPublicFunding", "validation.finance.other.funding.required");
        }
    }

    private void validateFundingAmount(BigDecimal fundingAmount, Errors errors) {
        if (fundingAmount == null) {
            rejectValue(errors, "fundingAmount", "validation.finance.funding.amount");
        } else if (fundingAmount.compareTo(BigDecimal.ZERO) < 1) {
            rejectValue(errors, "fundingAmount", "validation.finance.funding.amount");
        }
    }

    private void validateDate(BaseOtherFunding otherFunding, Errors errors, boolean isCompTypeOfgemAndFundingTypeThirdParty){
        String securedDate = otherFunding.getSecuredDate();
        if(StringUtils.isBlank(securedDate) && !isCompTypeOfgemAndFundingTypeThirdParty){
            rejectValue(errors, "securedDate", "validation.finance.funding.date.invalid");
        }else if(!isValidDate(securedDate, isCompTypeOfgemAndFundingTypeThirdParty)) {
            rejectValue(errors, "securedDate", "validation.finance.funding.date.invalid");
        }
    }

    private void validateFundingSource(String fundingSource, Errors errors){
        if(StringUtils.isBlank(fundingSource)){
            rejectValue(errors, "fundingSource", "validation.finance.funding.source.blank");
        }
    }

    private boolean userHasSelectedYes(final BaseOtherFunding otherFunding) {
        List<? extends FinanceRow> otherFundingRows = getRows(otherFunding);
        return !otherFundingRows.isEmpty() && "Yes".equals(otherFundingRows.get(0).getItem());
    }

    private Optional<ApplicationFinanceRow> applicationCost(BaseOtherFunding otherFunding) {
        return applicationFinanceRowRepository.findById(otherFunding.getId());
    }

    private ApplicationFinance applicationFinance(BaseOtherFunding otherFunding) {
        return applicationCost(otherFunding).get().getTarget();
    }

    private ProjectFinance projectFinance(BaseOtherFunding otherFunding) {
        ProjectFinanceRow projectCost = projectFinanceRowRepository.findById(otherFunding.getId()).get();
        return projectCost.getTarget();
    }

    private List<? extends FinanceRow> getRows(BaseOtherFunding otherFunding) {
        if (applicationCost(otherFunding).isPresent()) {
            return applicationFinanceRowRepository.findByTargetIdAndType(applicationFinance(otherFunding).getId(), otherFunding.getCostType());
        } else {
            return projectFinanceRowRepository.findByTargetIdAndType(projectFinance(otherFunding).getId(), otherFunding.getCostType());
        }
    }

    private boolean isCompTypeOfgemAndFundingTypeThirdParty(BaseOtherFunding otherFunding) {
        if (applicationCost(otherFunding).isPresent()) {
            return applicationFinance(otherFunding).getApplication().getCompetition().isThirdPartyOfgem();
        } else {
            return projectFinance(otherFunding).getProject().getApplication().getCompetition().isThirdPartyOfgem();
        }
    }

    private boolean isValidDate(final String input, boolean isCompTypeOfgemAndFundingTypeThirdParty){
        if (isCompTypeOfgemAndFundingTypeThirdParty && input.isBlank()) {
            return true;
        }

        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        format.setLenient(false);
        try {
            Date dt = format.parse(input);
            return format.format(dt).equals(input);
        } catch (ParseException e) {
            return false;
        }
    }
}
