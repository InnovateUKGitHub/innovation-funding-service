package org.innovateuk.ifs.application.forms.sections.yourfeccosts.form;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourFECCostsFormPopulator
 */
@Component
public class YourFECModelFormPopulator {

    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    YourFECModelFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public YourFECModelForm populate(long applicationId, long organisationId) {

        ApplicationFinanceResource applicationFinance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        Boolean fecModelEnabled = applicationFinance.getFecModelEnabled();
        return new YourFECModelForm(fecModelEnabled);
    }
}
