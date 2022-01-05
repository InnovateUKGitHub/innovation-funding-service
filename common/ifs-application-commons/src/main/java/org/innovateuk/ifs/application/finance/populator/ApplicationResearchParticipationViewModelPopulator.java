package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationResearchParticipationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationResearchParticipationViewModelPopulator {

    private CompetitionRestService competitionRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;

    public ApplicationResearchParticipationViewModelPopulator(CompetitionRestService competitionRestService,
                                                              ApplicationFinanceRestService applicationFinanceRestService) {
        this.competitionRestService = competitionRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public ApplicationResearchParticipationViewModel populate(long applicationId) {

        CompetitionResource competition = competitionRestService.getCompetitionForApplication(applicationId).getSuccess();

        return new ApplicationResearchParticipationViewModel(
                applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccess() ,
                competition,
                applicationId
        );

    }
}
