package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationResearchParticipationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationResearchParticipationViewModelPopulator {

    private ApplicationService applicationService;
    private CompetitionRestService competitionRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;

    public ApplicationResearchParticipationViewModelPopulator(ApplicationService applicationService,
                                                              CompetitionRestService competitionRestService,
                                                              ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationService = applicationService;
        this.competitionRestService = competitionRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public ApplicationResearchParticipationViewModel populate(long applicationId) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        return new ApplicationResearchParticipationViewModel(
                applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccess() ,
                competition
        );

    }
}
