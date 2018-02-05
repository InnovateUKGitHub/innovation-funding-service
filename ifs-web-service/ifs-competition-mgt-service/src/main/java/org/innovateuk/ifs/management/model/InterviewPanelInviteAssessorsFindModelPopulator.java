package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.InterviewPanelInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelAvailableAssessorRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelInviteAssessorsFindViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.management.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Panel Find view.
 */
@Component
public class InterviewPanelInviteAssessorsFindModelPopulator extends InterviewPanelInviteAssessorsModelPopulator<InterviewPanelInviteAssessorsFindViewModel> {

    @Autowired
    private InterviewPanelInviteRestService interviewPanelInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InterviewPanelInviteAssessorsFindViewModel populateModel(long competitionId,
                                                                     int page,
                                                                     String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        InterviewPanelInviteAssessorsFindViewModel model = super.populateModel(competition);

        AvailableAssessorPageResource pageResource = interviewPanelInviteRestService.getAvailableAssessors(
                competition.getId(),
                page)
                .getSuccessObjectOrThrowException();

        List<InterviewPanelAvailableAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);

        return model;
    }

    private InterviewPanelAvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource assessorInviteOverviewResource) {
        return new InterviewPanelAvailableAssessorRowViewModel(
                assessorInviteOverviewResource.getId(),
                assessorInviteOverviewResource.getName(),
                assessorInviteOverviewResource.getInnovationAreas(),
                assessorInviteOverviewResource.isCompliant(),
                assessorInviteOverviewResource.getBusinessType()
        );
    }

    @Override
    protected InterviewPanelInviteAssessorsFindViewModel createModel() {
        return new InterviewPanelInviteAssessorsFindViewModel();
    }
}
