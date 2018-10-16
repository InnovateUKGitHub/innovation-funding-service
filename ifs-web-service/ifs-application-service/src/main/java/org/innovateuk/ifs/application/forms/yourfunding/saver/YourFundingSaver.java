package org.innovateuk.ifs.application.forms.yourfunding.saver;

import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Component
public class YourFundingSaver {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private DefaultFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(long applicationId, YourFundingForm form, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();

        ValidationMessages messages = saveGrantClaim(finance, form).getSuccess();

        saveOtherFunding(finance, form, messages);

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }

    private RestResult<ValidationMessages> saveGrantClaim(ApplicationFinanceResource finance, YourFundingForm form) {
        GrantClaim claim = finance.getGrantClaim();
        boolean newRow = claim == null;
        if (claim == null) {
            claim = new GrantClaim();
        }

        if (form.getRequestingFunding()) {
            claim.setGrantClaimPercentage(form.getGrantClaimPercentage());
        } else {
            claim.setGrantClaimPercentage(0);
        }

        if (newRow) {
            return financeRowRestService.add(finance.getId(), form.getGrantClaimQuestionId(), claim);
        } else {
            return financeRowRestService.update(claim);
        }
    }

    private void saveOtherFunding(ApplicationFinanceResource finance, YourFundingForm form, ValidationMessages messages) {
        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        otherFundingCategory.getOtherFunding().setOtherPublicFunding(form.getOtherFunding() ? "Yes" : "No");
        messages.addAll(financeRowRestService.update(otherFundingCategory.getOtherFunding()).getSuccess());
        if (form.getOtherFunding()) {
            form.getOtherFundingRows().forEach((id, cost) -> {
                if (id == null) {
                    if (!cost.isBlank()) {
                        messages.addAll(financeRowRestService.add(finance.getId(), form.getOtherFundingQuestionId(), toFunding(cost)).getSuccess());
                    }
                } else {
                    messages.addAll(financeRowRestService.update(toFunding(cost)).getSuccess());
                }
            });
        }
    }

    private OtherFunding toFunding(OtherFundingRowForm form) {
        return new OtherFunding(form.getCostId(), null, form.getSource(), form.getDate(), form.getFundingAmount());
    }

    public void addOtherFundingRow(YourFundingForm form, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();

        Long costId = financeRowRestService.addWithResponse(finance.getId(), form.getOtherFundingQuestionId(), new OtherFunding()).getSuccess().getId();
        OtherFundingRowForm rowForm = new OtherFundingRowForm();
        rowForm.setCostId(costId);
        form.getOtherFundingRows().put(costId, rowForm);
    }

    public void removeOtherFundingRow(YourFundingForm form, Long costId) {
        form.getOtherFundingRows().remove(costId);
        if (costId != null) {
            financeRowRestService.delete(costId);
        }
    }
}
