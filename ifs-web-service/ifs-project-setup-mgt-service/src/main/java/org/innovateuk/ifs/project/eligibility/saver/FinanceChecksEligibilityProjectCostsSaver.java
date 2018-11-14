package org.innovateuk.ifs.project.eligibility.saver;

import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.AbstractYourProjectCostsSaver;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinanceChecksEligibilityProjectCostsSaver extends AbstractYourProjectCostsSaver {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Override
    protected BaseFinanceResource getFinanceResource(long projectId, long organisationId) {
        return projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
    }

    @Override
    protected FinanceRowRestService getFinanceRowService() {
        return projectFinanceRowRestService;
    }
}
