package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.AbstractYourProjectCostsFormPopulator;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FinanceChecksEligibilityProjectCostsFormPopulator extends AbstractYourProjectCostsFormPopulator {

    @Autowired
    private OverheadFileRestService overheadFileRestService;

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

    @Override
    protected Optional<FileEntryResource> overheadFile(long costId) {
        return overheadFileRestService.getOverheadFileDetailsUsingProjectFinanceRowId(costId).getOptionalSuccessObject();
    }
}
