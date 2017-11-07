package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.management.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Panel Find view.
 */
@Component
public class PanelInviteAssessorsFindModelPopulator extends PanelInviteAssessorsModelPopulator<PanelInviteAssessorsFindViewModel> {

    @Autowired
    private AssessmentPanelInviteRestService assessmentPanelInviteRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public PanelInviteAssessorsFindViewModel populateModel(long competitionId,
                                                           int page,
                                                           String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        PanelInviteAssessorsFindViewModel model = super.populateModel(competition);

        AvailableAssessorPageResource pageResource = assessmentPanelInviteRestService.getAvailableAssessors(
                competition.getId(),
                page)
                .getSuccessObjectOrThrowException();

        List<PanelAvailableAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);

        return model;
    }

    private PanelAvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource assessorInviteOverviewResource) {
        return new PanelAvailableAssessorRowViewModel(
                assessorInviteOverviewResource.getId(),
                assessorInviteOverviewResource.getName(),
                assessorInviteOverviewResource.getInnovationAreas(),
                assessorInviteOverviewResource.isCompliant(),
                assessorInviteOverviewResource.getBusinessType()
        );
    }

    @Override
    protected PanelInviteAssessorsFindViewModel createModel() {
        return new PanelInviteAssessorsFindViewModel();
    }
}
