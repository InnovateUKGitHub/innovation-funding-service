package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.AbstractYourProjectCostsFormPopulator;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationYourProjectCostsFormPopulator extends AbstractYourProjectCostsFormPopulator {
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OverheadFileRestService overheadFileRestService;

    @Override
    protected BaseFinanceResource getFinanceResource(long applicationId, long organisationId) {
        return applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
    }

    @Override
    protected boolean shouldAddEmptyRow() {
        return false;
    }

    @Override
    protected Optional<FileEntryResource> overheadFile(long costId) {
        return overheadFileRestService.getOverheadFileDetails(costId).getOptionalSuccessObject();
    }
}
