package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.populator.AbstractYourFundingFormPopulator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectYourFundingFormPopulator extends AbstractYourFundingFormPopulator {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public AbstractYourFundingForm populateForm(long projectId, long organisationId) {
        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        CompetitionResource competitionResource = competitionRestService.getCompetitionForProject(projectId).getSuccess();
        return super.populateForm(finance, competitionResource);
    }
}
