package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.viewmodel.PanelInviteAssessorsAcceptedViewModel;
import org.innovateuk.ifs.management.viewmodel.PanelOverviewAssessorRowViewModel;
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
public class PanelInviteAssessorsAcceptedModelPopulator extends PanelInviteAssessorsModelPopulator<PanelInviteAssessorsAcceptedViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    public PanelInviteAssessorsAcceptedViewModel populateModel(long competitionId,
                                                          int page,
                                                          String originQuery) {
        CompetitionResource competition = competitionsRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        PanelInviteAssessorsAcceptedViewModel model = super.populateModel(competition);

        AssessorInviteOverviewPageResource pageResource = competitionInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                empty(),
                singletonList(ACCEPTED),
                empty()
        )
                .getSuccessObjectOrThrowException();

        List<PanelOverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));
        model.setOriginQuery(originQuery);

        return model;
    }

    private PanelOverviewAssessorRowViewModel getRowViewModel(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        return new PanelOverviewAssessorRowViewModel(
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
    protected PanelInviteAssessorsAcceptedViewModel createModel() { return new PanelInviteAssessorsAcceptedViewModel(); }
}
