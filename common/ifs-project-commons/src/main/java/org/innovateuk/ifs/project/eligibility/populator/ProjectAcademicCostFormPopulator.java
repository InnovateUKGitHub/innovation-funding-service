package org.innovateuk.ifs.project.eligibility.populator;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AbstractAcademicCostFormPopulator;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProjectAcademicCostFormPopulator extends AbstractAcademicCostFormPopulator<ProjectFinanceResource> {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ProjectRestService projectRestService;

    public AcademicCostForm populate(AcademicCostForm form, long projectId, long organisationId) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        return populate(form, finance);
    }

    protected FinanceRowRestService financeRowRestService() {
        return projectFinanceRowRestService;
    }

    protected Long getFileEntryId(ProjectFinanceResource finance) {
        ProjectResource project = projectRestService.getProjectById(finance.getProject()).getSuccess();
        Optional<ApplicationFinanceResource> applicationFinanceResource = applicationFinanceRestService.getFinanceDetails(project.getApplication(), finance.getOrganisation()).getOptionalSuccessObject();
        return applicationFinanceResource.map(ApplicationFinanceResource::getFinanceFileEntry)
                .orElse(null);
    }
}
