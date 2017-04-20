package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
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

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    public InviteAssessorsOverviewViewModel populateModel(long competitionId,
                                                          int page,
                                                          Optional<Long> innovationArea,
                                                          Optional<ParticipantStatusResource> status,
                                                          Optional<Boolean> compliant,
                                                          String originQuery) {
        CompetitionResource competition = competitionsRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        InviteAssessorsOverviewViewModel model = super.populateModel(competition);

        List<InnovationAreaResource> innovationAreasOptions = categoryRestService.getInnovationAreas()
                .getSuccessObjectOrThrowException();

        AssessorInviteOverviewPageResource pageResource = competitionInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                innovationArea,
                status,
                compliant
        )
                .getSuccessObjectOrThrowException();

        List<OverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setInnovationAreaOptions(innovationAreasOptions);
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
