package org.innovateuk.ifs.interview.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationInviteRowViewModel;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite applicants for Assessment Interview Panel Invite view.
 */
@Component
public class InterviewAssignmentApplicationsSendModelPopulator {

    private InterviewAssignmentRestService interviewAssignmentRestService;
    private CompetitionRestService competitionRestService;

    @Autowired
    public InterviewAssignmentApplicationsSendModelPopulator(
            InterviewAssignmentRestService interviewAssignmentRestService,
            CompetitionRestService competitionRestService) {
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.competitionRestService = competitionRestService;
    }

    public InterviewAssignmentApplicationsSendViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewAssignmentStagedApplicationPageResource pageResource = interviewAssignmentRestService
                .getStagedApplications(competition.getId(), page)
                .getSuccess();

        String content = interviewAssignmentRestService.getEmailTemplate().getSuccess().getContent();

        InterviewAssignmentKeyStatisticsResource keyStatisticsResource =
                interviewAssignmentRestService.getKeyStatistics(competitionId).getSuccess();

        return new InterviewAssignmentApplicationsSendViewModel(
                competitionId,
                competition.getName(),
                StringUtils.join(competition.getInnovationAreaNames(), ", "),
                competition.getInnovationSectorName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                keyStatisticsResource,
                new PaginationViewModel(pageResource, originQuery),
                originQuery,
                content
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