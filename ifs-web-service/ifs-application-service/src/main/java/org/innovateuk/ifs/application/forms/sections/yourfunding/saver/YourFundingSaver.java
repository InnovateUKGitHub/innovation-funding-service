package org.innovateuk.ifs.application.forms.sections.yourfunding.saver;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static java.lang.Long.parseLong;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Component
public class YourFundingSaver {

    private final static Logger LOG = LoggerFactory.getLogger(YourFundingSaver.class);

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(long applicationId, YourFundingAmountForm form, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

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

    public ServiceResult<Void> save(long applicationId, YourFundingPercentageForm form, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

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

    public void addOtherFundingRow(YourFundingPercentageForm form) {
        OtherFundingRowForm rowForm = new OtherFundingRowForm();
        form.getOtherFundingRows().put(generateUnsavedRowId(), rowForm);
    }

    public void removeOtherFundingRowForm(YourFundingPercentageForm form, String costId) {
        form.getOtherFundingRows().remove(costId);
        removeOtherFundingRow(costId);
    }

    public void removeOtherFundingRow(String costId) {
        if (!costId.startsWith(UNSAVED_ROW_PREFIX)) {
            financeRowRestService.delete(parseLong(costId));
        }
    }

    public Optional<Long> autoSave(String field, String value, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();

        try {
            if (field.equals("grantClaimPercentage")) {
                finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
                GrantClaimPercentage grantClaim = (GrantClaimPercentage) finance.getGrantClaim();
                grantClaim.setPercentage(Integer.valueOf(value));
                financeRowRestService.update(grantClaim).getSuccess();
            } else if (field.equals("amount")) {
                finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
                GrantClaimAmount grantClaim = (GrantClaimAmount) finance.getGrantClaim();
                grantClaim.setAmount(new BigDecimal(value));
                financeRowRestService.update(grantClaim).getSuccess();
            } else if (field.startsWith("otherFundingRows")) {
                String id = field.substring(field.indexOf('[') + 1, field.indexOf(']'));
                String rowField = field.substring(field.indexOf("].") + 2);
                OtherFunding cost;

                if (id.startsWith(UNSAVED_ROW_PREFIX)) {
                    cost = (OtherFunding) financeRowRestService.create(new OtherFunding(finance.getId())).getSuccess();
                } else {
                    cost = (OtherFunding) financeRowRestService.get(Long.valueOf(id)).getSuccess();
                }

                if (rowField.equals("source")) {
                    cost.setFundingSource(value);
                } else if (rowField.equals("date")) {
                    cost.setSecuredDate(value);
                } else if (rowField.equals("fundingAmount")) {
                    cost.setFundingAmount(new BigDecimal(value));
                } else {
                    throw new IFSRuntimeException(String.format("Auto save other funding field not handled %s", rowField), Collections.emptyList());
                }
                financeRowRestService.update(cost);
                return Optional.of(cost.getId());
            } else {
                throw new IFSRuntimeException(String.format("Auto save field not handled %s", field), Collections.emptyList());
            }
        } catch (Exception e) {
            LOG.debug("Error auto saving", e);
            LOG.info(String.format("Unable to auto save field (%s) value (%s)", field, value));
        }
        return Optional.empty();
    }

    private void saveGrantClaimPercentage(ApplicationFinanceResource finance, YourFundingPercentageForm form, ValidationMessages messages) {
        GrantClaimPercentage claim = (GrantClaimPercentage) finance.getGrantClaim();
        if (form.getRequestingFunding()) {
            claim.setPercentage(form.getGrantClaimPercentage());
        } else {
            claim.setPercentage(0);
        }
        messages.addAll(financeRowRestService.update(claim).getSuccess());
    }


    private void saveGrantClaimAmount(ApplicationFinanceResource finance, YourFundingAmountForm form, ValidationMessages messages) {
        GrantClaimAmount claim = (GrantClaimAmount) finance.getGrantClaim();
        claim.setAmount(form.getAmount());
        messages.addAll(financeRowRestService.update(claim).getSuccess());
    }
    private void saveOtherFunding(ApplicationFinanceResource finance, AbstractYourFundingForm form, ValidationMessages messages) {
        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        otherFundingCategory.getOtherFunding().setOtherPublicFunding(form.getOtherFunding() ? "Yes" : "No");
        messages.addAll(financeRowRestService.update(otherFundingCategory.getOtherFunding()).getSuccess());
        if (form.getOtherFunding()) {
            form.getOtherFundingRows().forEach((id, cost) -> {
                if (id.startsWith(UNSAVED_ROW_PREFIX)) {
                    if (!cost.isBlank()) {
                        financeRowRestService.create(cost.toCost(finance.getId())).getSuccess();
                    }
                } else {
                    messages.addAll(financeRowRestService.update(cost.toCost(finance.getId())).getSuccess());
                }
            });
        }
    }

}
