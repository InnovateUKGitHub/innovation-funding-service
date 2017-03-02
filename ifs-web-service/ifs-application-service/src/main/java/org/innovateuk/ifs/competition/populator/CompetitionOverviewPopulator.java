package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.SectionViewModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
/**
 * Populator for creating the {@link CompetitionOverviewViewModel}
 */
@Service
public class CompetitionOverviewPopulator {
    private List<PublicContentSectionType> excludeSectionTypes = asList(PublicContentSectionType.SEARCH);

    public CompetitionOverviewViewModel populateViewModel(PublicContentItemResource publicContentItemResource, AbstractPublicSectionContentViewModel sectionContentViewModel) {
        CompetitionOverviewViewModel viewModel = new CompetitionOverviewViewModel();

        viewModel.setCompetitionOpenDate(publicContentItemResource.getCompetitionOpenDate());
        viewModel.setCompetitionCloseDate(publicContentItemResource.getCompetitionCloseDate());
        viewModel.setCompetitionTitle(publicContentItemResource.getCompetitionTitle());
        viewModel.setNonIfsUrl(publicContentItemResource.getNonIfsUrl());
        viewModel.setNonIfs(publicContentItemResource.getNonIfs());

        if(null != publicContentItemResource.getPublicContentResource()) {
            viewModel.setShortDescription(publicContentItemResource.getPublicContentResource().getShortDescription());
            viewModel.setCompetitionId(publicContentItemResource.getPublicContentResource().getCompetitionId());
        }

        viewModel.setAllContentSections(getContentSections(sectionContentViewModel.getSectionType()));
        viewModel.setCurrentSection(sectionContentViewModel);

        return viewModel;
    }

    private List<SectionViewModel> getContentSections(PublicContentSectionType currentSection) {
        List<SectionViewModel> contentSections = new ArrayList<>();

        Arrays.stream(PublicContentSectionType.values()).forEach(sectionType -> {
            if (!excludeSectionTypes.contains(sectionType)) {
                contentSections.add(new SectionViewModel(sectionType.getPath(), sectionType.getText(), currentSection.equals(sectionType)));
            }
        });

        return contentSections;
    }
}
