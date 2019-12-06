package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.populator.AbstractYourFundingFormPopulator;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectYourFundingFormPopulator extends AbstractYourFundingFormPopulator {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    public AbstractYourFundingForm populateForm(long projectId, long organisationId) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        return super.populateForm(finance);
    }
}
