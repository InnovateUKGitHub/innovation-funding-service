package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.JustificationForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationYourProjectCostsSaver extends AbstractYourProjectCostsSaver {
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(YourProjectCostsForm form, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ValidationMessages messages = saveProjectCostJustification(applicationId, organisation.getId(), form);
        return save(form, applicationId, organisation, messages);
    }

    @Override
    protected BaseFinanceResource getFinanceResource(long applicationId, long organisationId) {
        return applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
    }

    private ValidationMessages saveProjectCostJustification(long applicationId, long organisationId, YourProjectCostsForm form) {

        ValidationMessages messages = new ValidationMessages();
        JustificationForm justificationForm = form.getJustificationForm();
        if (justificationForm != null && justificationForm.getExceedAllowedLimit()) {

            ApplicationFinanceResource finance =
                    applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

            finance.setJustification(justificationForm.getExplanation());

            RestResult<ApplicationFinanceResource> result = applicationFinanceRestService.update(finance.getId(), finance);

            if (result.isFailure()) {
                messages.addErrors(result.getErrors());
            }
        }
        return messages;
    }

    @Override
    protected FinanceRowRestService getFinanceRowService() {
        return financeRowRestService;
    }
}
