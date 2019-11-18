package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YourFundingFormPopulator extends AbstractYourFundingFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    public AbstractYourFundingForm populateForm(long applicationId, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        return super.populateForm(finance);
    }
}
