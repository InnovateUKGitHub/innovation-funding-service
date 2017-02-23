package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.springframework.stereotype.Service;

/**
 * Populator for creating the {@link CompetitionOverviewViewModel}
 */
@Service
public class CompetitionOverviewPopulator {
    public CompetitionOverviewViewModel populateViewModel(PublicContentItemResource publicContentItemResource) {
        CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        viewModel.setCompetitionOpenDate(publicContentItemResource.getCompetitionOpenDate());
        viewModel.setCompetitionCloseDate(publicContentItemResource.getCompetitionCloseDate());
        viewModel.setCompetitionTitle(publicContentItemResource.getCompetitionTitle());

        if(null != publicContentItemResource.getPublicContentResource()) {
            viewModel.setShortDescription(publicContentItemResource.getPublicContentResource().getShortDescription());
            viewModel.setCompetitionId(publicContentItemResource.getPublicContentResource().getCompetitionId());
        }

        return viewModel;
    }
}
