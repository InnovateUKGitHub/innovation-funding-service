package org.innovateuk.ifs.application.forms.sections.yourfunding.saver;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;

import java.math.BigDecimal;

import static java.lang.Long.parseLong;
import static java.math.RoundingMode.HALF_UP;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public abstract class AbstractYourFundingSaver {

    protected abstract FinanceRowRestService getFinanceRowService();

    public void addOtherFundingRow(AbstractYourFundingForm form) {
        OtherFundingRowForm rowForm = new OtherFundingRowForm();
        form.getOtherFundingRows().put(generateUnsavedRowId(), rowForm);
    }

    public void removeOtherFundingRowForm(AbstractYourFundingForm form, String costId) {
        form.getOtherFundingRows().remove(costId);
        removeOtherFundingRow(costId);
    }

    public void removeOtherFundingRow(String costId) {
        if (!costId.startsWith(UNSAVED_ROW_PREFIX)) {
            getFinanceRowService().delete(parseLong(costId));
        }
    }
    protected ServiceResult<Void> save(BaseFinanceResource finance, YourFundingAmountForm form) {
        ValidationMessages messages = new ValidationMessages();

        saveGrantClaimAmount(finance, form, messages);

        if (form.getOtherFunding() != null) {
            saveOtherFunding(finance, form, messages);
        }

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }

    }

    protected ServiceResult<Void> save(BaseFinanceResource finance, YourFundingPercentageForm form) {

        ValidationMessages messages = new ValidationMessages();

        if (form.getRequestingFunding() != null) {
            saveGrantClaimPercentage(finance, form, messages);
        }

        if (form.getOtherFunding() != null) {
            saveOtherFunding(finance, form, messages);
        }

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }

    private void saveGrantClaimPercentage(BaseFinanceResource finance, YourFundingPercentageForm form, ValidationMessages messages) {
        GrantClaimPercentage claim = (GrantClaimPercentage) finance.getGrantClaim();
        if (form.getRequestingFunding()) {
            claim.setPercentage(form.getGrantClaimPercentage().setScale(2, HALF_UP));
        } else {
            claim.setPercentage(BigDecimal.ZERO);
        }
        messages.addAll(getFinanceRowService().update(claim).getSuccess());
    }

    private void saveGrantClaimAmount(BaseFinanceResource finance, YourFundingAmountForm form, ValidationMessages messages) {
        GrantClaimAmount claim = (GrantClaimAmount) finance.getGrantClaim();
        claim.setAmount(form.getAmount());
        messages.addAll(getFinanceRowService().update(claim).getSuccess());
    }

    private void saveOtherFunding(BaseFinanceResource finance, AbstractYourFundingForm form, ValidationMessages messages) {
        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        otherFundingCategory.getOtherFunding().setOtherPublicFunding(form.getOtherFunding() ? "Yes" : "No");
        messages.addAll(getFinanceRowService().update(otherFundingCategory.getOtherFunding()).getSuccess());
        if (form.getOtherFunding()) {
            form.getOtherFundingRows().forEach((id, cost) -> {
                if (id.startsWith(UNSAVED_ROW_PREFIX)) {
                    if (!cost.isBlank()) {
                        getFinanceRowService().create(cost.toCost(finance.getId())).getSuccess();
                    }
                } else {
                    messages.addAll(getFinanceRowService().update(cost.toCost(finance.getId())).getSuccess());
                }
            });
        }
    }

}
