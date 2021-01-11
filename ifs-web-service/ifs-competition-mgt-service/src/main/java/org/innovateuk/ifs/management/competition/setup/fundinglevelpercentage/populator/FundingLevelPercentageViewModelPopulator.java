package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.viewmodel.FundingLevelPercentageViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Service
public class FundingLevelPercentageViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competition) {
        List<ResearchCategoryResource> allResearchCategories = categoryRestService.getResearchCategories().getSuccess();
        return new FundingLevelPercentageViewModel(generalViewModel,
                allResearchCategories.stream().filter(cat -> competition.getResearchCategories().contains(cat.getId())).collect(Collectors.toList()),
                asList(OrganisationSize.values()));
    }
}
