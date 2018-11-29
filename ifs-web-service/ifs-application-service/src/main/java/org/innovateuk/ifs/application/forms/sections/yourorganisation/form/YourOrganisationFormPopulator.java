package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationForm
 */
@Component
public class YourOrganisationFormPopulator {

    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    YourOrganisationFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public YourOrganisationForm populate(long applicationId, long organisationId) {

        ApplicationFinanceResource applicationFinance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        String postcode = applicationFinance.getWorkPostcode();
        return new YourOrganisationForm(postcode);
    }
}
