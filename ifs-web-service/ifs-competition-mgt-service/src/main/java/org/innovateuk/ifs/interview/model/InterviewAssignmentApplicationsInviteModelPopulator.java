package org.innovateuk.ifs.interview.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationInviteRowViewModel;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsInviteViewModel;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Panel Invite view.
 */
@Component
public class InterviewAssignmentApplicationsInviteModelPopulator {

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Autowired
    private CompetitionRestService competitionRestService;


    public InterviewAssignmentApplicationsInviteViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewAssignmentStagedApplicationPageResource pageResource = interviewAssignmentRestService
                .getStagedApplications(competition.getId(), page)
                .getSuccess();

        return new InterviewAssignmentApplicationsInviteViewModel(
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

    private InterviewAssignmentApplicationInviteRowViewModel getRowViewModel(InterviewAssignmentStagedApplicationResource interviewAssignmentStagedApplicationResource) {
        return new InterviewAssignmentApplicationInviteRowViewModel(
                interviewAssignmentStagedApplicationResource.getId(),
                interviewAssignmentStagedApplicationResource.getApplicationId(),
                interviewAssignmentStagedApplicationResource.getApplicationName(),
                interviewAssignmentStagedApplicationResource.getLeadOrganisationName()
        );
    }
}