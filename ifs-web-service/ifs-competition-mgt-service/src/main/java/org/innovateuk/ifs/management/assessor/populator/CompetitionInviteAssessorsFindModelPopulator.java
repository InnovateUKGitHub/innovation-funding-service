package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.assessor.viewmodel.CompetitionAvailableAssessorRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.CompetitionInviteAssessorsFindViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors 'Find' view.
 */
@Component
public class CompetitionInviteAssessorsFindModelPopulator extends CompetitionInviteAssessorsModelPopulator<CompetitionInviteAssessorsFindViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public CompetitionInviteAssessorsFindViewModel populateModel(long competitionId,
                                                                 int page,
                                                                 String assessorNameFilter) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        CompetitionInviteAssessorsFindViewModel model = super.populateModel(competition);

        List<InnovationSectorResource> innovationSectors = categoryRestService.getInnovationSectors().getSuccess();

        AvailableAssessorPageResource pageResource = competitionInviteRestService.getAvailableAssessors(competition.getId(), page, assessorNameFilter)
                .getSuccess();

        List<CompetitionAvailableAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setInnovationSectorOptions(innovationSectors);
        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);

        return model;
    }

    private CompetitionAvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource availableAssessorResource) {
        return new CompetitionAvailableAssessorRowViewModel(
                availableAssessorResource.getId(),
                availableAssessorResource.getName(),
                availableAssessorResource.getInnovationAreas(),
                availableAssessorResource.isCompliant(),
                availableAssessorResource.getEmail(),
                availableAssessorResource.getBusinessType()
        );
    }

    @Override
    protected CompetitionInviteAssessorsFindViewModel createModel() {
        return new CompetitionInviteAssessorsFindViewModel();
    }
}
