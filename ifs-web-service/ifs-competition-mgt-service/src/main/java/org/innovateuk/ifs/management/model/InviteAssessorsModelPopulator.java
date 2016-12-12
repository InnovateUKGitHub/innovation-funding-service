package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors view.
 */
@Component
abstract class InviteAssessorsModelPopulator<ViewModelType extends InviteAssessorsViewModel> {

    public ViewModelType populateModel(CompetitionResource competition) {
        ViewModelType model = populateCompetitionDetails(createModel(), competition);
        populateStatistics(model);
        populateCompetitionInnovationSectorAndArea(model);
        return model;
    }

    protected abstract ViewModelType createModel();

    private ViewModelType populateCompetitionDetails(ViewModelType model, CompetitionResource competition) {
        model.setCompetitionId(competition.getId());
        model.setCompetitionName(competition.getName());
        return model;
    }

    private ViewModelType populateStatistics(ViewModelType model) {
        model.setAssessorsInvited(60);
        model.setAssessorsAccepted(23);
        model.setAssessorsDeclined(3);
        model.setAssessorsStaged(6);
        return model;
    }

    private ViewModelType populateCompetitionInnovationSectorAndArea(ViewModelType model) {
        model.setInnovationSector("Health and life sciences");
        model.setInnovationArea("Agriculture and food");
        return model;
    }
}
