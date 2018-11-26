package org.innovateuk.ifs.application.forms.yourfunding.saver;

import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Long.parseLong;
import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_ID;
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
    private DefaultFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(long applicationId, YourFundingForm form, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();

        ValidationMessages messages = new ValidationMessages();

        if (form.getRequestingFunding() != null) {
            saveGrantClaim(finance, form, messages);
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

    public void addOtherFundingRow(YourFundingForm form, long applicationId, UserResource user) {
        OtherFundingRowForm rowForm = new OtherFundingRowForm();
        form.getOtherFundingRows().put(UNSAVED_ROW_ID + UUID.randomUUID().toString(), rowForm);
    }

    public void removeOtherFundingRowForm(YourFundingForm form, String costId) {
        form.getOtherFundingRows().remove(costId);
        removeOtherFundingRow(costId);
    }

    public void removeOtherFundingRow(String costId) {
        if (!costId.startsWith(UNSAVED_ROW_ID)) {
            financeRowRestService.delete(parseLong(costId));
        }
    }

    public Optional<Long> autoSave(String field, String value, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();

        try {
            if (field.equals("grantClaimPercentage")) {
                finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
                GrantClaim grantClaim = finance.getGrantClaim();
                grantClaim.setGrantClaimPercentage(Integer.valueOf(value));
                financeRowRestService.update(grantClaim).getSuccess();
            } else if (field.startsWith("otherFundingRows")) {
                String id = field.substring(field.indexOf('[') + 1, field.indexOf(']'));
                String rowField = field.substring(field.indexOf("].") + 2);
                OtherFunding cost;

                if (id.startsWith(UNSAVED_ROW_ID)) {
                    cost = (OtherFunding) financeRowRestService.addWithResponse(finance.getId(), new OtherFunding()).getSuccess();
                } else {
                    cost = (OtherFunding) financeRowRestService.getCost(Long.valueOf(id)).getSuccess();
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

    private void saveGrantClaim(ApplicationFinanceResource finance, YourFundingForm form, ValidationMessages messages) {
        GrantClaim claim = finance.getGrantClaim();
        if (form.getRequestingFunding()) {
            claim.setGrantClaimPercentage(form.getGrantClaimPercentage());
        } else {
            claim.setGrantClaimPercentage(0);
        }
        messages.addAll(financeRowRestService.update(claim).getSuccess());
    }

    private void saveOtherFunding(ApplicationFinanceResource finance, YourFundingForm form, ValidationMessages messages) {
        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        otherFundingCategory.getOtherFunding().setOtherPublicFunding(form.getOtherFunding() ? "Yes" : "No");
        messages.addAll(financeRowRestService.update(otherFundingCategory.getOtherFunding()).getSuccess());
        if (form.getOtherFunding()) {
            form.getOtherFundingRows().forEach((id, cost) -> {
                if (id.startsWith(UNSAVED_ROW_ID)) {
                    if (!cost.isBlank()) {
                        messages.addAll(financeRowRestService.add(finance.getId(), form.getOtherFundingQuestionId(), cost.toCost()).getSuccess());
                    }
                } else {
                    messages.addAll(financeRowRestService.update(cost.toCost()).getSuccess());
                }
            });
        }
    }

}
