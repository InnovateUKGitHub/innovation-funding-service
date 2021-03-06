package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YourFundingFormPopulator extends AbstractYourFundingFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public AbstractYourFundingForm populateForm(long applicationId, long organisationId) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionForApplication(applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        return super.populateForm(finance, competitionResource);
    }
}
