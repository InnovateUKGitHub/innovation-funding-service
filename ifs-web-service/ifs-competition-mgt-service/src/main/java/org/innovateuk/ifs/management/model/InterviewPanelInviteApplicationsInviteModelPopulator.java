package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.service.InterviewPanelRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInvitePageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInviteResource;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelApplicationInviteRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelInviteApplicationsInviteViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Panel Invite view.
 */
@Component
public class InterviewPanelInviteApplicationsInviteModelPopulator {

    @Autowired
    private InterviewPanelRestService interviewPanelRestService;

    @Autowired
    private CompetitionRestService competitionRestService;


    public InterviewPanelInviteApplicationsInviteViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewPanelCreatedInvitePageResource pageResource = interviewPanelRestService
                .getCreatedInvites(competition.getId(), page)
                .getSuccess();

        return new InterviewPanelInviteApplicationsInviteViewModel(
                competitionId,
                competition.getName(),
                competition.getInnovationSectorName(),
                StringUtils.join(competition.getInnovationAreaNames(), ", "),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                0,
                0,
                new PaginationViewModel(pageResource, originQuery),
                originQuery
        );
    }

    private InterviewPanelApplicationInviteRowViewModel getRowViewModel(InterviewPanelCreatedInviteResource interviewPanelCreatedInviteResource) {
        return new InterviewPanelApplicationInviteRowViewModel(
                interviewPanelCreatedInviteResource.getId(),
                interviewPanelCreatedInviteResource.getApplicationId(),
                interviewPanelCreatedInviteResource.getApplicationName(),
                interviewPanelCreatedInviteResource.getLeadOrganisationName()
        );
    }
}