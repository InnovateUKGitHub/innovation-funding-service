package com.worth.ifs.management.model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.viewmodel.InviteAssessorsOverviewViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors 'Overview' view.
 */
@Component
public class InviteAssessorsOverviewModelPopulator extends InviteAssessorsModelPopulator<InviteAssessorsOverviewViewModel> {

    @Override
    public InviteAssessorsOverviewViewModel populateModel(CompetitionResource competition) {
        InviteAssessorsOverviewViewModel model = super.populateModel(competition);
        return model;
    }

    @Override
    protected InviteAssessorsOverviewViewModel createModel() {
        return new InviteAssessorsOverviewViewModel();
    }
}