package org.innovateuk.ifs.review.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInvitePageResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.management.assessor.viewmodel.InvitedAssessorRowViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.review.viewmodel.ReviewInviteAssessorsInviteViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Panel Invite view.
 */
@Component
public class ReviewInviteAssessorsInviteModelPopulator extends ReviewInviteAssessorsModelPopulator<ReviewInviteAssessorsInviteViewModel> {

    @Autowired
    private ReviewInviteRestService reviewInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ReviewInviteAssessorsInviteViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        ReviewInviteAssessorsInviteViewModel model = super.populateModel(competition);

        AssessorCreatedInvitePageResource pageResource = reviewInviteRestService.getCreatedInvites(competition.getId(), page)
                .getSuccess();

        List<InvitedAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource, originQuery));

        return model;
    }

    private InvitedAssessorRowViewModel getRowViewModel(AssessorCreatedInviteResource assessorCreatedInviteResource) {
        return new InvitedAssessorRowViewModel(
                assessorCreatedInviteResource.getId(),
                assessorCreatedInviteResource.getName(),
                assessorCreatedInviteResource.getInnovationAreas(),
                assessorCreatedInviteResource.isCompliant(),
                assessorCreatedInviteResource.getEmail(),
                assessorCreatedInviteResource.getInviteId()
        );
    }

    @Override
    protected ReviewInviteAssessorsInviteViewModel createModel() {
        return new ReviewInviteAssessorsInviteViewModel();
    }
}
