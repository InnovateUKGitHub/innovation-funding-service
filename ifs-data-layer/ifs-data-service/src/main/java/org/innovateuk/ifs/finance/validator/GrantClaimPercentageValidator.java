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

        if(response.getPercentage() == null) {
            rejectValue(errors, "percentage", "org.hibernate.validator.constraints.NotBlank.message");
            return;
        }

        if (response.getPercentage().scale() > 2) {
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

        if (BigDecimal.valueOf(max).compareTo(response.getPercentage()) < 0) {
            rejectValue(errors, "percentage", "validation.finance.grant.claim.percentage.max", max);
        } else if(BigDecimal.valueOf(max).compareTo(response.getPercentage()) > 1) {
            rejectValue(errors, "percentage", "validation.field.percentage.max.value.or.higher", 0);
        }
    }

}
