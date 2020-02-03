package org.innovateuk.ifs.finance.validator;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL_PLACES;

/**
 * This class validates the GrantClaim.
 */
@Component
public class GrantClaimPercentageValidator implements Validator {

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return GrantClaimPercentage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GrantClaimPercentage response = (GrantClaimPercentage) target;

//        if (response.isRequestingFunding()) {

            if (response.getPercentage() == null) {
                rejectValue(errors, "percentage", "org.hibernate.validator.constraints.NotBlank.message");
                return;
            }

            if (response.getPercentage().scale() > MAX_DECIMAL_PLACES) {
                rejectValue(errors, "percentage", "validation.finance.percentage");
                return;
            }

            Finance finance = applicationFinanceRowRepository.findById(response.getId())
                    .map(ApplicationFinanceRow::getTarget)
                    .map(Finance.class::cast)
                    .orElseGet(() -> projectFinanceRowRepository.findById(response.getId()).get().getTarget());
            Integer max = finance.getMaximumFundingLevel();
            if (max == null) {
                rejectValue(errors, "percentage", "validation.grantClaimPercentage.maximum.not.defined");
                return;
            }

            if (response.getPercentage().compareTo(BigDecimal.ZERO) < 0) {
                rejectValue(errors, "percentage", "validation.field.percentage.max.value.or.higher", 0);
            }
            if (response.getPercentage().compareTo(BigDecimal.valueOf(max)) > 0) {
                rejectValue(errors, "percentage", "validation.finance.grant.claim.percentage.max", max);
            }
//        }
    }

}
