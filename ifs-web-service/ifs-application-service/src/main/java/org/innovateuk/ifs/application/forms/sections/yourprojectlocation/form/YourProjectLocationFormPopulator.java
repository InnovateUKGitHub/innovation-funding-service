package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourProjectLocationForm
 */
@Component
public class YourProjectLocationFormPopulator {

    private final ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    YourProjectLocationFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public YourProjectLocationForm populate(long applicationId, long organisationId) {

        ApplicationFinanceResource applicationFinance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        String postcode = applicationFinance.getWorkPostcode();
        return new YourProjectLocationForm(postcode);
    }
}
