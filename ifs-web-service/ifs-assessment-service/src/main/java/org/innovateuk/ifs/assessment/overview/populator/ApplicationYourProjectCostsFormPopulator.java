package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.AbstractYourProjectCostsFormPopulator;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationYourProjectCostsFormPopulator extends AbstractYourProjectCostsFormPopulator {
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected BaseFinanceResource getFinanceResource(long applicationId, long organisationId) {
        return applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
    }

    @Override
    protected boolean shouldAddEmptyRow() {
        return false;
    }
}
