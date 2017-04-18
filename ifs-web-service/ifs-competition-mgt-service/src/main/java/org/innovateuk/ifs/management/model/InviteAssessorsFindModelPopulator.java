package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.AvailableAssessorRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors 'Find' view.
 */
@Component
public class InviteAssessorsFindModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsFindViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    public InviteAssessorsFindViewModel populateModel(long competitionId,
                                                      int page,
                                                      Optional<Long> innovationArea,
                                                      String originQuery) {
        CompetitionResource competition = competitionsRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        InviteAssessorsFindViewModel model = super.populateModel(competition);

        List<InnovationSectorResource> innovationSectors = categoryRestService.getInnovationSectors().getSuccessObjectOrThrowException();

        AvailableAssessorPageResource pageResource = competitionInviteRestService.getAvailableAssessors(competition.getId(), page, innovationArea)
                .getSuccessObjectOrThrowException();

        List<AvailableAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setInnovationSectorOptions(innovationSectors);
        model.setAssessors(assessors);
        model.setPagination(new PaginationViewModel(pageResource, originQuery));

        return model;
    }

    private AvailableAssessorRowViewModel getRowViewModel(AvailableAssessorResource availableAssessorResource) {
        return new AvailableAssessorRowViewModel(
                availableAssessorResource.getId(),
                availableAssessorResource.getName(),
                availableAssessorResource.getInnovationAreas(),
                availableAssessorResource.isCompliant(),
                availableAssessorResource.getEmail(),
                availableAssessorResource.getBusinessType()
        );
    }

    @Override
    protected InviteAssessorsFindViewModel createModel() {
        return new InviteAssessorsFindViewModel();
    }
}
