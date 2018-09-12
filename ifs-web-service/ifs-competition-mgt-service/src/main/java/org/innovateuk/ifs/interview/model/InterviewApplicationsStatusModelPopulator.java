package org.innovateuk.ifs.interview.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationStatusRowViewModel;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationStatusViewModel;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Assign Application to Interview Panel 'view status' view.
 */
@Component
public class InterviewApplicationsStatusModelPopulator extends InterviewApplicationsModelPopulator {

    private InterviewAssignmentRestService interviewAssignmentRestService;
    private CompetitionRestService competitionRestService;

    @Autowired
    public InterviewApplicationsStatusModelPopulator(
            InterviewAssignmentRestService interviewAssignmentRestService,
            CompetitionRestService competitionRestService) {
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.competitionRestService = competitionRestService;
    }

    public InterviewAssignmentApplicationStatusViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewAssignmentApplicationPageResource pageResource = interviewAssignmentRestService
                .getAssignedApplications(competition.getId(), page)
                .getSuccess();

        return new InterviewAssignmentApplicationStatusViewModel(
                competitionId,
                competition.getName(),
                StringUtils.join(competition.getInnovationAreaNames(), ", "),
                competition.getInnovationSectorName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                getKeyStatistics(competitionId),
                new Pagination(pageResource, originQuery),
                originQuery
        );
    }

    private InterviewAssignmentApplicationStatusRowViewModel getRowViewModel(InterviewAssignmentApplicationResource interviewAssignmentApplicationResource) {
        return new InterviewAssignmentApplicationStatusRowViewModel(
                interviewAssignmentApplicationResource.getId(),
                interviewAssignmentApplicationResource.getApplicationId(),
                interviewAssignmentApplicationResource.getApplicationName(),
                interviewAssignmentApplicationResource.getLeadOrganisationName(),
                interviewAssignmentApplicationResource.getStatus()
        );
    }
}
