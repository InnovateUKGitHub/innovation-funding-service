package org.innovateuk.ifs.interview.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationRowViewModel;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsFindViewModel;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.management.cookie.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Panel Find view.
 */
@Component
public class InterviewApplicationsFindModelPopulator extends InterviewApplicationsModelPopulator {

    private InterviewAssignmentRestService interviewAssignmentRestService;
    private CompetitionRestService competitionRestService;

    @Autowired
    public InterviewApplicationsFindModelPopulator(InterviewAssignmentRestService interviewAssignmentRestService,
                                                   CompetitionRestService competitionRestService) {
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.competitionRestService = competitionRestService;
    }

    public InterviewAssignmentApplicationsFindViewModel populateModel(long competitionId,
                                                                      int page,
                                                                      String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        AvailableApplicationPageResource pageResource = interviewAssignmentRestService.getAvailableApplications(
                competition.getId(),
                page)
                .getSuccess();

        List<InterviewAssignmentApplicationRowViewModel> applications = simpleMap(pageResource.getContent(), this::getRowViewModel);

        return new InterviewAssignmentApplicationsFindViewModel(
                competitionId,
                competition.getName(),
                StringUtils.join(competition.getInnovationAreaNames(), ", "),
                competition.getInnovationSectorName(),
                applications,
                super.getKeyStatistics(competitionId),
                new Pagination(pageResource, originQuery), originQuery, pageResource.getTotalElements() > SELECTION_LIMIT);
    }

    private InterviewAssignmentApplicationRowViewModel getRowViewModel(AvailableApplicationResource availableApplicationResource) {
        return new InterviewAssignmentApplicationRowViewModel(
                availableApplicationResource.getId(),
                availableApplicationResource.getName(),
                availableApplicationResource.getLeadOrganisation()
        );
    }
}