package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Populator for creating the {@link CompetitionOverviewViewModel}
 */
@Service
public class CompetitionOverviewPopulator {
    public CompetitionOverviewViewModel populateViewModel(PublicContentItemResource publicContentItemResource, boolean userIsLoggedIn,
                                                          List<AbstractPublicSectionContentViewModel> sectionContentViewModels) {
        CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        viewModel.setCompetitionOpenDate(publicContentItemResource.getCompetitionOpenDate());
        viewModel.setCompetitionCloseDate(publicContentItemResource.getCompetitionCloseDate());
        viewModel.setRegistrationCloseDate(publicContentItemResource.getCompetitionCloseDate().minusDays(7));
        viewModel.setCompetitionTitle(publicContentItemResource.getCompetitionTitle());
        viewModel.setNonIfsUrl(publicContentItemResource.getNonIfsUrl());
        viewModel.setNonIfs(publicContentItemResource.getNonIfs());
        viewModel.setUserIsLoggedIn(userIsLoggedIn);

        if(publicContentItemResource.getNonIfs()) {
            viewModel.setShowApplyButton(nonIfsCompetitionIsOpen(viewModel.getCompetitionOpenDate(), viewModel.getRegistrationCloseDate()));
        } else {
            viewModel.setShowApplyButton(publicContentItemResource.getCompetitionIsOpen());
        }

        if(null != publicContentItemResource.getPublicContentResource()) {
            viewModel.setShortDescription(publicContentItemResource.getPublicContentResource().getShortDescription());
            viewModel.setCompetitionId(publicContentItemResource.getPublicContentResource().getCompetitionId());
        }

        viewModel.setAllSections(sectionContentViewModels);

        return viewModel;
    }

    private Boolean nonIfsCompetitionIsOpen(LocalDateTime competitionOpenDate, LocalDateTime registrationCloseDate) {
        return LocalDateTime.now().isAfter(competitionOpenDate) && LocalDateTime.now().isBefore(registrationCloseDate);
    }
}
