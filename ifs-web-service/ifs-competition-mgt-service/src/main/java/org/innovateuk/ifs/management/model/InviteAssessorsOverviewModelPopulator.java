package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsOverviewViewModel;
import org.innovateuk.ifs.management.viewmodel.OverviewAssessorRowViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Invite assessors 'Overview' view.
 */
@Component
public class InviteAssessorsOverviewModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsOverviewViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Override
    public InviteAssessorsOverviewViewModel populateModel(CompetitionResource competition) {
        InviteAssessorsOverviewViewModel model = super.populateModel(competition);
        model.setAssessors(getAssessors(competition));
        return model;
    }

    private List<OverviewAssessorRowViewModel> getAssessors(CompetitionResource competition) {
        return competitionInviteRestService.getInvitationOverview(competition.getId()).getSuccessObject()
                .stream()
                .map(this::getRowViewModel)
                .collect(toList());
    }

    private OverviewAssessorRowViewModel getRowViewModel(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        return new OverviewAssessorRowViewModel(assessorInviteOverviewResource.getName(), assessorInviteOverviewResource.getInnovationAreaName(), assessorInviteOverviewResource.isCompliant(), assessorInviteOverviewResource.getBusinessType(), assessorInviteOverviewResource.getStatus(), assessorInviteOverviewResource.getDetails());
    }

    @Override
    protected InviteAssessorsOverviewViewModel createModel() {
        return new InviteAssessorsOverviewViewModel();
    }
}
