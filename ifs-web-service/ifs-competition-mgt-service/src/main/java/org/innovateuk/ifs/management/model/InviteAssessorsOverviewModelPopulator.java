package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsOverviewViewModel;
import org.innovateuk.ifs.management.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors 'Overview' view.
 */
@Component
public class InviteAssessorsOverviewModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsOverviewViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    public InviteAssessorsOverviewViewModel populateModel(CompetitionResource competition,
                                                          int page,
                                                          Optional<Long> innovationArea,
                                                          Optional<ParticipantStatusResource> status,
                                                          Optional<Boolean> contract,
                                                          String originQuery) {
        InviteAssessorsOverviewViewModel model = super.populateModel(competition);

        AssessorInviteOverviewPageResource pageResource = competitionInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                innovationArea,
                status,
                contract
        )
                .getSuccessObjectOrThrowException();

        List<OverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));

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
                assessorInviteOverviewResource.getDetails());
    }

    @Override
    protected InviteAssessorsOverviewViewModel createModel() {
        return new InviteAssessorsOverviewViewModel();
    }
}
