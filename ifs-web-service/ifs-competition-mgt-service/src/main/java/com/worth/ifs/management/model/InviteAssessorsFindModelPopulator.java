package com.worth.ifs.management.model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors 'Find' view.
 */
@Component
public class InviteAssessorsFindModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsFindViewModel> {

    @Override
    public InviteAssessorsFindViewModel populateModel(CompetitionResource competition) {
        InviteAssessorsFindViewModel model = super.populateModel(competition);

        // TODO INFUND-6392 Populate the assessors list

        return model;
    }

    @Override
    protected InviteAssessorsFindViewModel createModel() {
        return new InviteAssessorsFindViewModel();
    }
}