package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInvitePageResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.management.assessor.viewmodel.CompetitionInviteAssessorsInviteViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InvitedAssessorRowViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors 'Invite' view.
 */
@Component
public class CompetitionInviteAssessorsInviteModelPopulator extends CompetitionInviteAssessorsModelPopulator<CompetitionInviteAssessorsInviteViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public CompetitionInviteAssessorsInviteViewModel populateModel(long competitionId, int page) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        CompetitionInviteAssessorsInviteViewModel model = super.populateModel(competition);

        AssessorCreatedInvitePageResource pageResource = competitionInviteRestService.getCreatedInvites(competition.getId(), page)
                .getSuccess();

        List<InvitedAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource));
        model.setInnovationSectorOptions(getInnovationSectors());

        return model;
    }

    private List<InnovationSectorResource> getInnovationSectors() {
        return categoryRestService.getInnovationSectors().getSuccess();
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
    protected CompetitionInviteAssessorsInviteViewModel createModel() {
        return new CompetitionInviteAssessorsInviteViewModel();
    }
}
