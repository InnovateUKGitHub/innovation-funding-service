package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentSectionViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Populator for creating the {@link CompetitionOverviewViewModel}
 */
@Service
public class CompetitionOverviewPopulator {

    private static final PublicContentSectionType MAIN_SECTIONTYPE = PublicContentSectionType.SUMMARY;
    private final List<PublicContentSectionType> excludeSectionTypes = asList(PublicContentSectionType.SEARCH);

    private Map<PublicContentSectionType, AbstractPublicContentSectionViewModelPopulator> sectionModelPopulators;

    @Autowired
    public void setSectionPopulator(Collection<AbstractPublicContentSectionViewModelPopulator> populators) {
        sectionModelPopulators = populators.stream().collect(Collectors.toMap(p -> p.getType(), Function.identity()));
    }

    public CompetitionOverviewViewModel populateViewModel(PublicContentItemResource publicContentItemResource, boolean userIsLoggedIn) {
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

        viewModel.setAllSections(getSectionPopulators(publicContentItemResource));

        return viewModel;
    }

    private List<AbstractPublicSectionContentViewModel> getSectionPopulators(PublicContentItemResource publicContentItemResource) {
        List<AbstractPublicSectionContentViewModel> sectionViewModels = new ArrayList<>();

        Arrays.stream(PublicContentSectionType.values()).forEach(sectionType -> {
            if (!excludeSectionTypes.contains(sectionType)) {
                AbstractPublicSectionContentViewModel sectionViewModel = getPopulator(sectionType)
                        .populate(publicContentItemResource.getPublicContentResource(), publicContentItemResource.getNonIfs(), sectionType, MAIN_SECTIONTYPE);
                sectionViewModels.add(sectionViewModel);
            }
        });

        return sectionViewModels;
    }

    private Boolean nonIfsCompetitionIsOpen(LocalDateTime competitionOpenDate, LocalDateTime registrationCloseDate) {
        return LocalDateTime.now().isAfter(competitionOpenDate) && LocalDateTime.now().isBefore(registrationCloseDate);
    }

    private AbstractPublicContentSectionViewModelPopulator getPopulator(PublicContentSectionType sectionType) {
        if(excludeSectionTypes.contains(sectionType)) {
            return null;
        }
        return sectionModelPopulators.getOrDefault(sectionType, null);
    }
}
