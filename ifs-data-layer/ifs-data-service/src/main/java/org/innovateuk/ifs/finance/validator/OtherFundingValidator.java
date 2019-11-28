package org.innovateuk.ifs.finance.validator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
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
        return OtherFunding.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OtherFunding otherFunding = (OtherFunding) target;

        String otherFundingSelection = otherFunding.getOtherPublicFunding();
        validateOtherPublicFunding(otherFundingSelection, errors);

        boolean userHasSelectedYesToOtherFunding = userHasSelectedYes(otherFunding);
        String fundingSource = otherFunding.getFundingSource();
        BigDecimal fundingAmount = otherFunding.getFundingAmount();
        if (userHasSelectedYesToOtherFunding && fundingSource != null && !fundingSource.equals(OTHER_FUNDING)) {
            validateDate(otherFunding, errors);
            validateFundingSource(fundingSource, errors);
            validateFundingAmount(fundingAmount, errors);
        } else if(userHasSelectedYesToOtherFunding && fundingSource == null) {
        	validateDate(otherFunding, errors);
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

    private void validateDate(OtherFunding otherFunding, Errors errors){
        String securedDate = otherFunding.getSecuredDate();
        if(StringUtils.isBlank(securedDate)){
            rejectValue(errors, "securedDate", "validation.finance.funding.date.invalid");
        }else if(!isValidDate(securedDate)) {
            rejectValue(errors, "securedDate", "validation.finance.funding.date.invalid");
        }
    }

    private void validateFundingSource(String fundingSource, Errors errors){
        if(StringUtils.isBlank(fundingSource)){
            rejectValue(errors, "fundingSource", "validation.finance.funding.source.blank");
        }
    }

    private boolean userHasSelectedYes(final OtherFunding otherFunding) {
        List<? extends FinanceRow> otherFundingRows = getRows(otherFunding);
        return !otherFundingRows.isEmpty() && "Yes".equals(otherFundingRows.get(0).getItem());
    }

    private List<? extends FinanceRow> getRows(OtherFunding otherFunding) {
        Optional<ApplicationFinanceRow> applicationCost = applicationFinanceRowRepository.findById(otherFunding.getId());
        if (applicationCost.isPresent()) {
            ApplicationFinance applicationFinance = applicationCost.get().getTarget();
            return applicationFinanceRowRepository.findByTargetIdAndType(applicationFinance.getId(), FinanceRowType.OTHER_FUNDING);
        } else {
            ProjectFinanceRow projectCost = projectFinanceRowRepository.findById(otherFunding.getId()).get();
            ProjectFinance projectFinance = projectCost.getTarget();
            return projectFinanceRowRepository.findByTargetIdAndType(projectFinance.getId(), FinanceRowType.OTHER_FUNDING);
        }
    }

    private boolean isValidDate(final String input){
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        format.setLenient(false);
        try {
            Date dt = format.parse(input);
            return format.format(dt).equals(input);
        } catch(ParseException e){
            return false;
        }
    }
}
