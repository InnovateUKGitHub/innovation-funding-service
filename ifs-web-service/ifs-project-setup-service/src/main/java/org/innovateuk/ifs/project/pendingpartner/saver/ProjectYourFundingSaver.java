package org.innovateuk.ifs.project.pendingpartner.saver;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.saver.AbstractYourFundingSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectYourFundingSaver extends AbstractYourFundingSaver {

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Override
    protected FinanceRowRestService getFinanceRowService() {
        return projectFinanceRowRestService;
    }

    public ServiceResult<Void> save(long projectId, long organisationId, YourFundingAmountForm form) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        return super.save(finance, form);
    }

    public ServiceResult<Void> save(long projectId, long organisationId, YourFundingPercentageForm form) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        return super.save(finance, form);
    }
}
