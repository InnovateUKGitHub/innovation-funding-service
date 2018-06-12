package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAvailableAssessorRowViewModel;
import org.innovateuk.ifs.interview.viewmodel.InterviewInviteAssessorsFindViewModel;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Panel Find view.
 */
@Component
public class InterviewInviteAssessorsFindModelPopulator extends InterviewInviteAssessorsModelPopulator<InterviewInviteAssessorsFindViewModel> {

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InterviewInviteAssessorsFindViewModel populateModel(long competitionId,
                                                               int page,
                                                               String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewInviteAssessorsFindViewModel model = super.populateModel(competition);

        AvailableAssessorPageResource pageResource = interviewInviteRestService.getAvailableAssessors(
                competition.getId(),
                page)
                .getSuccess();

        List<InterviewAvailableAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource, originQuery));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);

        return model;
    }

    private InterviewAvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource assessorInviteOverviewResource) {
        return new InterviewAvailableAssessorRowViewModel(
                assessorInviteOverviewResource.getId(),
                assessorInviteOverviewResource.getName(),
                assessorInviteOverviewResource.getInnovationAreas(),
                assessorInviteOverviewResource.isCompliant(),
                assessorInviteOverviewResource.getBusinessType()
        );
    }

    @Override
    protected InterviewInviteAssessorsFindViewModel createModel() {
        return new InterviewInviteAssessorsFindViewModel();
    }
}
