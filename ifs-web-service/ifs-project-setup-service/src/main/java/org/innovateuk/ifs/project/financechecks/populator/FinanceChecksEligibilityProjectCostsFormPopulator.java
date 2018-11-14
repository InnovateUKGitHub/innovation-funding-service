package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.AbstractYourProjectCostsFormPopulator;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinanceChecksEligibilityProjectCostsFormPopulator extends AbstractYourProjectCostsFormPopulator {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Override
    protected BaseFinanceResource getFinanceResource(long projectId, long organisationId) {
        return projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
    }

    @Override
    protected boolean shouldAddEmptyRow() {
        return false;
    }
}
