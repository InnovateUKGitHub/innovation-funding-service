package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.ReviewPanelInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.management.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.viewmodel.ReviewPanelInviteAssessorsOverviewViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.management.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Assessment Panel Invite assessors 'Overview' view.
 */
@Component
public class PanelInviteAssessorsOverviewModelPopulator extends PanelInviteAssessorsModelPopulator<ReviewPanelInviteAssessorsOverviewViewModel> {

    @Autowired
    private ReviewPanelInviteRestService reviewPanelInviteRestService;

    @Autowired
    private CompetitionRestService competitionsRestService;

    public ReviewPanelInviteAssessorsOverviewViewModel populateModel(long competitionId,
                                                                     int page,
                                                                     String originQuery) {
        CompetitionResource competition = competitionsRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        ReviewPanelInviteAssessorsOverviewViewModel model = super.populateModel(competition);

        AssessorInviteOverviewPageResource pageResource = reviewPanelInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                asList(REJECTED, PENDING)
        ).getSuccessObjectOrThrowException();

        List<OverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);
        model.setOriginQuery(originQuery);

        return model;
    }

    private OverviewAssessorRowViewModel getRowViewModel(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        return new OverviewAssessorRowViewModel(
                assessorInviteOverviewResource.getId(),
                assessorInviteOverviewResource.getName(),
                assessorInviteOverviewResource.getInnovationAreas(),
                assessorInviteOverviewResource.isCompliant(),
                assessorInviteOverviewResource.getBusinessType(),
                assessorInviteOverviewResource.getStatus(),
                assessorInviteOverviewResource.getDetails(),
                assessorInviteOverviewResource.getInviteId());
    }

    @Override
    protected ReviewPanelInviteAssessorsOverviewViewModel createModel() {
        return new ReviewPanelInviteAssessorsOverviewViewModel();
    }
}
