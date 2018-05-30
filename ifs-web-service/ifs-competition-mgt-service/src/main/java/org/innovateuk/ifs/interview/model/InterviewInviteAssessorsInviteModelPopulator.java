package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewInviteAssessorsInviteViewModel;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInvitePageResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.management.assessor.viewmodel.InvitedAssessorRowViewModel;
import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Invite view.
 */
@Component
public class InterviewInviteAssessorsInviteModelPopulator extends InterviewInviteAssessorsModelPopulator<InterviewInviteAssessorsInviteViewModel> {

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InterviewInviteAssessorsInviteViewModel populateModel(long competitionId, int page, String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewInviteAssessorsInviteViewModel model = super.populateModel(competition);

        AssessorCreatedInvitePageResource pageResource = interviewInviteRestService.getCreatedInvites(competition.getId(), page)
                .getSuccess();

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
    protected InterviewInviteAssessorsInviteViewModel createModel() {
        return new InterviewInviteAssessorsInviteViewModel();
    }
}
