package org.innovateuk.ifs.application.forms.yourfunding.validator;

import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.TRUE;

@Component
public class YourFundingFormValidator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public void validate(YourFundingForm form, Errors errors, UserResource user, long applicationId) {

        ValidationUtils.rejectIfEmpty(errors, "requestingFunding", "validation.finance.funding.requesting.blank");
        if (TRUE.equals(form.getRequestingFunding())) {
            validateFundingLevel(form, errors, user, applicationId);
        }

        ValidationUtils.rejectIfEmpty(errors, "otherFunding", "validation.finance.other.funding.required");
        if (TRUE.equals(form.getOtherFunding())) {
            validateOtherFundingRows(form.getOtherFundingRows(), errors);
        }

        if (!TRUE.equals(form.getTermsAgreed())) {
            errors.rejectValue("termsAgreed", "validation.field.must.not.be.blank");
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

    private void validateFundingLevel(YourFundingForm form, Errors errors,  UserResource user, long applicationId) {
        ValidationUtils.rejectIfEmpty(errors, "grantClaimPercentage", "validation.field.must.not.be.blank");
        if (form.getGrantClaimPercentage() != null) {
            if (form.getGrantClaimPercentage() <= 0) {
                errors.rejectValue("grantClaimPercentage", "validation.finance.grant.claim.percentage.min");
            } else {
                OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
                ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();
                if (form.getGrantClaimPercentage() > finance.getMaximumFundingLevel()) {
                    errors.rejectValue("grantClaimPercentage", "validation.finance.grant.claim.percentage.max",  new String[] {String.valueOf(finance.getMaximumFundingLevel())}, "");
                }
            }
        }
    }
}
