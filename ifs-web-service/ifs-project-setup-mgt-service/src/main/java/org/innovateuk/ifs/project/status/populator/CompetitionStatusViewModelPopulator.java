package org.innovateuk.ifs.project.status.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.internal.populator.InternalProjectSetupRowPopulator;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

/**
 * This class represents a populated CompetitionStatusViewModel.
 */
@Component
public class CompetitionStatusViewModelPopulator {

    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;
    private StatusRestService statusRestService;
    private CompetitionRestService competitionRestService;
    private InternalProjectSetupRowPopulator internalProjectSetupRowPopulator;

    private CompetitionStatusViewModelPopulator() {
    }

    @Autowired
    public CompetitionStatusViewModelPopulator(CompetitionPostSubmissionRestService competitionPostSubmissionRestService,
                                               StatusRestService statusRestService,
                                               CompetitionRestService competitionRestService,
                                               InternalProjectSetupRowPopulator internalProjectSetupRowPopulator) {
        this.competitionPostSubmissionRestService = competitionPostSubmissionRestService;
        this.statusRestService = statusRestService;
        this.competitionRestService = competitionRestService;
        this.internalProjectSetupRowPopulator = internalProjectSetupRowPopulator;
    }

    public CompetitionStatusViewModel populate(UserResource user, Long competitionId, String applicationSearchString) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        final boolean hasProjectFinanceRole = user.hasRole(PROJECT_FINANCE);
        long openQueryCount = hasProjectFinanceRole ? competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccess() : 0L;
        long pendingSpendProfilesCount = hasProjectFinanceRole ? competitionPostSubmissionRestService.countPendingSpendProfiles(competitionId).getSuccess() : 0;
        List<ProjectStatusResource> projectStatusResources = statusRestService.getCompetitionStatus(competitionId, StringUtils.trim(applicationSearchString)).getSuccess();

        List<InternalProjectSetupRow> internalProjectSetupRows = internalProjectSetupRowPopulator.populate(projectStatusResources, competition, user);

        return new CompetitionStatusViewModel(competition,
                hasProjectFinanceRole,
                openQueryCount,
                pendingSpendProfilesCount,
                applicationSearchString,
                internalProjectSetupRows);
    }

}
