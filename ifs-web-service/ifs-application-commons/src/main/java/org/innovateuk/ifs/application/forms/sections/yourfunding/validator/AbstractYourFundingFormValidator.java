package org.innovateuk.ifs.application.forms.sections.yourfunding.validator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.*;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
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
import static org.innovateuk.ifs.util.NumberUtils.getBigDecimalFormatted;

public class AbstractYourFundingFormValidator {

    protected void validate(AbstractYourFundingForm form, Errors errors, Supplier<BaseFinanceResource> financeSupplier, BigDecimal maximumFundingSought) {

        if (form instanceof YourFundingPercentageForm) {
            validateYourFundingPercentageForm((YourFundingPercentageForm) form, errors, financeSupplier, maximumFundingSought);
        }
        if (form instanceof YourFundingAmountForm) {
            validateYourFundingAmountForm((YourFundingAmountForm) form, errors, financeSupplier, maximumFundingSought);
        }

        ValidationUtils.rejectIfEmpty(errors, "otherFunding", "validation.finance.other.funding.required");
        if (TRUE.equals(form.getOtherFunding())) {
            validateOtherFundingRows(form.getOtherFundingRows(), errors);
        }
    }

    private void validateOtherFundingRows(Map<String, BaseOtherFundingRowForm> rows, Errors errors) {
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

    private boolean isBlankButNotOnlyRow(BaseOtherFundingRowForm row, Map<String, BaseOtherFundingRowForm> rows) {
        return row.isBlank() && rows.size() > 1;
    }

    private void validateOtherFundingSource(String id, BaseOtherFundingRowForm row, Errors errors) {
        if (isNullOrEmpty(row.getSource())) {
            errors.rejectValue(String.format("otherFundingRows[%s].source", id), "validation.finance.funding.source.blank");
        }
    }

    private void validateOtherFundingAmount(String id, BaseOtherFundingRowForm row, Errors errors) {
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

    private void validateOtherFundingDate(String id, BaseOtherFundingRowForm row, Errors errors) {
        if (isNullOrEmpty(row.getDate())) {
            errors.rejectValue(String.format("otherFundingRows[%s].date", id), "validation.finance.funding.date.invalid");
        }
        Pattern pattern = Pattern.compile("^(?:((0[1-9]|1[012])-[0-9]{4})|)$");
        Matcher m = pattern.matcher(row.getDate());
        if (!m.matches()) {
            errors.rejectValue(String.format("otherFundingRows[%s].date", id), "validation.finance.funding.date.invalid");
        }
    }

    private void validateYourFundingAmountForm(YourFundingAmountForm form, Errors errors, Supplier<BaseFinanceResource> financeSupplier, BigDecimal maximumFundingSought) {
        ValidationUtils.rejectIfEmpty(errors, "amount", "validation.finance.funding.sought.required");
        if (form.getAmount() != null) {
            if (form.getAmount().compareTo(BigDecimal.ONE) < 0) {
                errors.rejectValue("amount", "validation.finance.funding.sought.min");
            }
            if (maximumFundingSought != null) {
                if (form.getAmount().compareTo(maximumFundingSought) > 0) {
                    errors.rejectValue("amount", "validation.finance.grant.claim.percentage.more.than.funding.amount", new String[]{getBigDecimalFormatted(maximumFundingSought)}, "");
                }
            }
        }
    }

    private void validateYourFundingPercentageForm(YourFundingPercentageForm form, Errors errors, Supplier<BaseFinanceResource> financeSupplier, BigDecimal maximumFundingSought) {
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
                    if (maximumFundingSought != null) {
                        GrantClaimPercentage grantClaimPercentage = new GrantClaimPercentage(finance.getGrantClaim().getId(), form.getGrantClaimPercentage(), finance.getGrantClaim().getTargetId());
                        if (grantClaimPercentage.calculateFundingSought(finance.getTotal(), finance.getTotalOtherFunding()).compareTo(maximumFundingSought) > 0) {
                            errors.rejectValue("grantClaimPercentage", "validation.finance.grant.claim.percentage.more.than.funding.amount", new String[]{getBigDecimalFormatted(maximumFundingSought)}, "");
                        }
                    }
                }
            }
        }
    }
}
