package org.innovateuk.ifs.project.eligibility.saver;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.saver.AbstractAcademicCostSaver;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectAcademicCostsSaver extends AbstractAcademicCostSaver {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Override
    protected FinanceRowRestService financeRowRestService() {
        return projectFinanceRowRestService;
    }

    public ServiceResult<Void> save(AcademicCostForm form, long projectId, long organisationId) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        return save(form, finance);
    }
}