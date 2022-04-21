package org.innovateuk.ifs.application.forms.sections.yourfunding.saver;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.BaseOtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;

import java.math.BigDecimal;

import static java.lang.Long.parseLong;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL_PLACES;

public abstract class AbstractYourFundingSaver<R extends BaseOtherFunding, T extends BaseOtherFundingRowForm<R>> {

    protected abstract FinanceRowRestService getFinanceRowService();

    @SuppressWarnings("unchecked")
    public void addOtherFundingRow(AbstractYourFundingForm<R, T> form) {
        T row = null;
        if (FinanceRowType.OTHER_FUNDING == form.otherFundingType()) {
            row = (T) new OtherFundingRowForm();
        } else if (FinanceRowType.PREVIOUS_FUNDING == form.otherFundingType()) {
            row = (T) new PreviousFundingRowForm();
        }
        form.getOtherFundingRows().put(generateUnsavedRowId(), row);
    }

    public void removeOtherFundingRowForm(AbstractYourFundingForm<R, T> form, String costId) {
        form.getOtherFundingRows().remove(costId);
        removeOtherFundingRow(costId);
    }

    public void removeOtherFundingRow(String costId) {
        if (!costId.startsWith(UNSAVED_ROW_PREFIX)) {
            getFinanceRowService().delete(parseLong(costId));
        }
    }

    protected ServiceResult<Void> save(BaseFinanceResource finance, AbstractYourFundingAmountForm<R, T> form) {
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

    protected ServiceResult<Void> save(BaseFinanceResource finance, AbstractYourFundingPercentageForm<R, T> form) {

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

    private void saveGrantClaimPercentage(BaseFinanceResource finance, AbstractYourFundingPercentageForm<R, T> form, ValidationMessages messages) {
        GrantClaimPercentage claim = (GrantClaimPercentage) finance.getGrantClaim();
        if (form.getRequestingFunding()) {
            claim.setPercentage(ofNullable(form.getGrantClaimPercentage()).map(v -> v.setScale(MAX_DECIMAL_PLACES, HALF_UP)).orElse(BigDecimal.ZERO));
        } else {
            claim.setPercentage(BigDecimal.ZERO);
        }
        messages.addAll(getFinanceRowService().update(claim).getSuccess());
    }

    private void saveGrantClaimAmount(BaseFinanceResource finance, AbstractYourFundingAmountForm<R, T> form, ValidationMessages messages) {
        GrantClaimAmount claim = (GrantClaimAmount) finance.getGrantClaim();
        claim.setAmount(form.getAmount());
        messages.addAll(getFinanceRowService().update(claim).getSuccess());
    }

    private void saveOtherFunding(BaseFinanceResource finance, AbstractYourFundingForm<R, T> form, ValidationMessages messages) {
        BaseOtherFundingCostCategory otherFundingCategory = (BaseOtherFundingCostCategory) finance.getFinanceOrganisationDetails(form.otherFundingType());
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
