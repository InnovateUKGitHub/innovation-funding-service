package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.management.assessor.viewmodel.CompetitionInviteAssessorsOverviewViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors 'Overview' view.
 */
@Component
public class CompetitionInviteAssessorsOverviewModelPopulator extends CompetitionInviteAssessorsModelPopulator<CompetitionInviteAssessorsOverviewViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public CompetitionInviteAssessorsOverviewViewModel populateModel(long competitionId,
                                                                     int page,
                                                                     Optional<ParticipantStatusResource> status,
                                                                     Optional<Boolean> compliant,
                                                                     Optional<String> assessorName) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        CompetitionInviteAssessorsOverviewViewModel model = super.populateModel(competition);

        List<InnovationAreaResource> innovationAreasOptions = categoryRestService.getInnovationAreas()
                .getSuccess();

        List<ParticipantStatusResource> statuses = status.map(Collections::singletonList)
                .orElseGet(() -> asList(REJECTED, PENDING));

        AssessorInviteOverviewPageResource pageResource = competitionInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                statuses,
                compliant,
                assessorName).getSuccess();

        List<OverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setInnovationAreaOptions(innovationAreasOptions);
        model.setPagination(new Pagination(pageResource));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);
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
    protected CompetitionInviteAssessorsOverviewViewModel createModel() {
        return new CompetitionInviteAssessorsOverviewViewModel();
    }
}
