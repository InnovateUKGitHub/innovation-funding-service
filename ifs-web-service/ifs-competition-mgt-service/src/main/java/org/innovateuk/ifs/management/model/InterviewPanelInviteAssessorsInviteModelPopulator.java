package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInvitePageResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelInviteAssessorsInviteViewModel;
import org.innovateuk.ifs.management.viewmodel.InvitedAssessorRowViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Invite view.
 */
@Component
public class InterviewPanelInviteAssessorsInviteModelPopulator extends InterviewPanelInviteAssessorsModelPopulator<InterviewPanelInviteAssessorsInviteViewModel> {

    @Autowired
    private AssessmentPanelInviteRestService assessmentPanelInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InterviewPanelInviteAssessorsInviteViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        InterviewPanelInviteAssessorsInviteViewModel model = super.populateModel(competition);

        AssessorCreatedInvitePageResource pageResource = assessmentPanelInviteRestService.getCreatedInvites(competition.getId(), page)
                .getSuccessObjectOrThrowException();

        List<InvitedAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));

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
    protected InterviewPanelInviteAssessorsInviteViewModel createModel() {
        return new InterviewPanelInviteAssessorsInviteViewModel();
    }
}
