package com.worth.ifs.management.model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.viewmodel.InviteAssessorsInviteViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors 'Invite' view.
 */
@Component
public class InviteAssessorsInviteModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsInviteViewModel> {

    @Override
    public InviteAssessorsInviteViewModel populateModel(CompetitionResource competition) {
        InviteAssessorsInviteViewModel model = super.populateModel(competition);

        // TODO INFUND-6414

        return model;
    }

    @Override
    protected InviteAssessorsInviteViewModel createModel() {
        return new InviteAssessorsInviteViewModel();
    }
}