package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsInviteViewModel;
import org.innovateuk.ifs.management.viewmodel.InvitedAssessorRowViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Invite assessors 'Invite' view.
 */
@Component
public class InviteAssessorsInviteModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsInviteViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public InviteAssessorsInviteViewModel populateModel(CompetitionResource competition) {
        InviteAssessorsInviteViewModel model = super.populateModel(competition);
        model.setAssessors(getAssessors(competition));
        model.setInnovationSectorOptions(getInnovationSectors());
        return model;
    }

    private List<InvitedAssessorRowViewModel> getAssessors(CompetitionResource competition) {
        return competitionInviteRestService.getCreatedInvites(competition.getId()).getSuccessObject()
                .stream()
                .map(this::getRowViewModel)
                .collect(toList());
    }

    private List<InnovationSectorResource> getInnovationSectors() {
        return categoryService.getInnovationSectors();
    }


    private InvitedAssessorRowViewModel getRowViewModel(AssessorCreatedInviteResource assessorCreatedInviteResource) {
        return new InvitedAssessorRowViewModel(
                assessorCreatedInviteResource.getName(),
                assessorCreatedInviteResource.getInnovationAreas(),
                assessorCreatedInviteResource.isCompliant(),
                assessorCreatedInviteResource.getEmail(),
                assessorCreatedInviteResource.getInviteId()
        );
    }

    @Override
    protected InviteAssessorsInviteViewModel createModel() {
        return new InviteAssessorsInviteViewModel();
    }
}
