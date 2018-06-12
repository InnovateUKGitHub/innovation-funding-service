package org.innovateuk.ifs.review.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.review.viewmodel.ReviewAvailableAssessorRowViewModel;
import org.innovateuk.ifs.review.viewmodel.ReviewInviteAssessorsFindViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.management.cookie.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Panel Find view.
 */
@Component
public class ReviewInviteAssessorsFindModelPopulator extends ReviewInviteAssessorsModelPopulator<ReviewInviteAssessorsFindViewModel> {

    @Autowired
    private ReviewInviteRestService reviewInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ReviewInviteAssessorsFindViewModel populateModel(long competitionId,
                                                            int page,
                                                            String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        ReviewInviteAssessorsFindViewModel model = super.populateModel(competition);

        AvailableAssessorPageResource pageResource = reviewInviteRestService.getAvailableAssessors(
                competition.getId(),
                page)
                .getSuccess();

        List<ReviewAvailableAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource, originQuery));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);

        return model;
    }

    private ReviewAvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource assessorInviteOverviewResource) {
        return new ReviewAvailableAssessorRowViewModel(
                assessorInviteOverviewResource.getId(),
                assessorInviteOverviewResource.getName(),
                assessorInviteOverviewResource.getInnovationAreas(),
                assessorInviteOverviewResource.isCompliant(),
                assessorInviteOverviewResource.getBusinessType()
        );
    }

    @Override
    protected ReviewInviteAssessorsFindViewModel createModel() {
        return new ReviewInviteAssessorsFindViewModel();
    }
}
