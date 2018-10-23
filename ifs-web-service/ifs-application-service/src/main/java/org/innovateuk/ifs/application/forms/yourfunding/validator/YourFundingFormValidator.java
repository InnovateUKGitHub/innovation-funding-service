package org.innovateuk.ifs.application.forms.yourfunding.validator;

import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.TRUE;

@Component
public class YourFundingFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return YourFundingForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        YourFundingForm form = (YourFundingForm) target;

        ValidationUtils.rejectIfEmpty(errors, "requestingFunding", "validation.field.must.not.be.blank");
        if (TRUE.equals(form.getRequestingFunding())) {
            validateFundingLevel(form, errors);
        }

        ValidationUtils.rejectIfEmpty(errors, "otherFunding", "validation.field.must.not.be.blank");
        if (TRUE.equals(form.getOtherFunding())) {
            validateOtherFundingRows(form, errors);
        }

        if (!TRUE.equals(form.getTermsAgreed())) {
            errors.rejectValue("termsAgreed", "validation.field.must.not.be.blank");
        }
    }

    private void validateOtherFundingRows(YourFundingForm form, Errors errors) {
        if (form.getOtherFundingRows() != null) {
            form.getOtherFundingRows().forEach((id, row) -> {
                if (!row.isBlank()) {
                    validateOtherFundingDate(id, row, errors);
                    validateOtherFundingSource(id, row, errors);
                    validateOtherFundingAmount(id, row, errors);
                }
            });
        }
    }

    private void validateOtherFundingSource(String id, OtherFundingRowForm row, Errors errors) {
        if (isNullOrEmpty(row.getSource())) {
            errors.rejectValue(String.format("otherFundingRows[%s].source", id), "validation.finance.funding.source.blank");
        }
    }

    private void validateOtherFundingAmount(String id, OtherFundingRowForm row, Errors errors) {
        if (row.getFundingAmount() == null) {
            errors.rejectValue(String.format("otherFundingRows[%s].fundingAmount", id), "validation.finance.funding.amount");
        } else {
            int integerPartLength = row.getFundingAmount().precision() - row.getFundingAmount().scale();
            if (integerPartLength >= 20) {
                errors.rejectValue(String.format("otherFundingRows[%s].fundingAmount", id), "validation.finance.funding.amount.invalid");
            }
        }
    }

    private void validateOtherFundingDate(String id, OtherFundingRowForm row, Errors errors) {
        if (isNullOrEmpty(row.getDate())) {
            errors.rejectValue(String.format("otherFundingRows[%s].date", id), "validation.finance.funding.date.invalid");
        }
        Pattern pattern = Pattern.compile("^(?:((0[1-9]|1[012])-[0-9]{4})|)$");
        Matcher m = pattern.matcher(row.getDate());
        if (!m.matches()) {
            errors.rejectValue(String.format("otherFundingRows[%s].date", id), "validation.finance.funding.date.invalid");
        }
    }

    private void validateFundingLevel(YourFundingForm form, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "otherFunding", "validation.field.must.not.be.blank");
        if (form.getGrantClaimPercentage() != null) {
            if (form.getGrantClaimPercentage() < 0) {
                errors.rejectValue("otherFunding", "validation.finance.funding.level.min",  new String[] {"1"}, "");
            }
        }
    }
}
