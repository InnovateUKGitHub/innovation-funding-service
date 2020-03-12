package org.innovateuk.ifs.application.forms.sections.yourfunding.validator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL_PLACES;

public class AbstractYourFundingFormValidator {

    protected void validate(AbstractYourFundingForm form, Errors errors, Supplier<BaseFinanceResource> financeSupplier) {

        if (form instanceof YourFundingPercentageForm) {
            validateYourFundingPercentageForm((YourFundingPercentageForm) form, errors, financeSupplier);
        }
        if (form instanceof YourFundingAmountForm) {
            validateYourFundingAmountForm((YourFundingAmountForm) form, errors);
        }

        ValidationUtils.rejectIfEmpty(errors, "otherFunding", "validation.finance.other.funding.required");
        if (TRUE.equals(form.getOtherFunding())) {
            validateOtherFundingRows(form.getOtherFundingRows(), errors);
        }
    }

    private void validateOtherFundingRows(Map<String, OtherFundingRowForm> rows, Errors errors) {
        if (rows == null || rows.isEmpty()) {
            errors.rejectValue("otherFunding", "validation.finance.min.row.other.funding.single");
        } else {
            rows.forEach((id, row) -> {
                if (!isBlankButNotOnlyRow(row, rows)) {
                    validateOtherFundingDate(id, row, errors);
                    validateOtherFundingSource(id, row, errors);
                    validateOtherFundingAmount(id, row, errors);
                }
            });
        }
    }

    private boolean isBlankButNotOnlyRow(OtherFundingRowForm row, Map<String, OtherFundingRowForm> rows) {
        return row.isBlank() && rows.size() > 1;
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
            if (row.getFundingAmount().compareTo(BigDecimal.ONE) < 0) {
                errors.rejectValue(String.format("otherFundingRows[%s].fundingAmount", id), "validation.field.max.value.or.higher", new Object[]{"1"}, "");
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

    private void validateYourFundingAmountForm(YourFundingAmountForm form, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "amount", "validation.finance.funding.sought.required");
        if (form.getAmount() != null && form.getAmount().compareTo(BigDecimal.ONE) < 0) {
            errors.rejectValue("amount", "validation.finance.funding.sought.min");
        }
    }

    private void validateYourFundingPercentageForm(YourFundingPercentageForm form, Errors errors, Supplier<BaseFinanceResource> financeSupplier) {
        ValidationUtils.rejectIfEmpty(errors, "requestingFunding", "validation.finance.funding.requesting.blank");
        if (TRUE.equals(form.getRequestingFunding())) {
            ValidationUtils.rejectIfEmpty(errors, "grantClaimPercentage", "validation.field.must.not.be.blank");
            if (form.getGrantClaimPercentage() != null) {

                if (form.getGrantClaimPercentage().scale() > MAX_DECIMAL_PLACES) {
                    errors.rejectValue("grantClaimPercentage", "validation.finance.percentage");
                }

                if (form.getGrantClaimPercentage().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.rejectValue("grantClaimPercentage", "validation.finance.grant.claim.percentage.min");
                } else {
                    BaseFinanceResource finance = financeSupplier.get();
                    if (form.getGrantClaimPercentage().compareTo(BigDecimal.valueOf(finance.getMaximumFundingLevel())) > 0) {
                        errors.rejectValue("grantClaimPercentage", "validation.finance.grant.claim.percentage.max", new String[]{String.valueOf(finance.getMaximumFundingLevel())}, "");
                    }
                }
            }
        }
    }
}
