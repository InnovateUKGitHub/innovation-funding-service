package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.service.InterviewPanelRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationResource;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelApplicationInviteRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelApplicationsInviteViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Panel Invite view.
 */
@Component
public class InterviewPanelApplicationsInviteModelPopulator {

    @Autowired
    private InterviewPanelRestService interviewPanelRestService;

    @Autowired
    private CompetitionRestService competitionRestService;


    public InterviewPanelApplicationsInviteViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewPanelStagedApplicationPageResource pageResource = interviewPanelRestService
                .getStagedApplications(competition.getId(), page)
                .getSuccess();

        return new InterviewPanelApplicationsInviteViewModel(
                competitionId,
                competition.getName(),
                StringUtils.join(competition.getInnovationAreaNames(), ", "),
                competition.getInnovationSectorName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                0,
                0,
                new PaginationViewModel(pageResource, originQuery),
                originQuery
        );
    }

    private InterviewPanelApplicationInviteRowViewModel getRowViewModel(InterviewPanelStagedApplicationResource interviewPanelStagedApplicationResource) {
        return new InterviewPanelApplicationInviteRowViewModel(
                interviewPanelStagedApplicationResource.getId(),
                interviewPanelStagedApplicationResource.getApplicationId(),
                interviewPanelStagedApplicationResource.getApplicationName(),
                interviewPanelStagedApplicationResource.getLeadOrganisationName()
        );
    }
}