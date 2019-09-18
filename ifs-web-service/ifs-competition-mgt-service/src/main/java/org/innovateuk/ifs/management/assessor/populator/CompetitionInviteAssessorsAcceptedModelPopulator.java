package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsAcceptedViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors 'Accepted' view.
 */
@Component
public class CompetitionInviteAssessorsAcceptedModelPopulator extends CompetitionInviteAssessorsModelPopulator<InviteAssessorsAcceptedViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InviteAssessorsAcceptedViewModel populateModel(long competitionId,
                                                          int page) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InviteAssessorsAcceptedViewModel model = super.populateModel(competition);

        AssessorInviteOverviewPageResource pageResource = competitionInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                singletonList(ACCEPTED),
                empty(),
                empty()
        )
                .getSuccess();

        List<OverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource));

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
    protected InviteAssessorsAcceptedViewModel createModel() { return new InviteAssessorsAcceptedViewModel(); }
}
