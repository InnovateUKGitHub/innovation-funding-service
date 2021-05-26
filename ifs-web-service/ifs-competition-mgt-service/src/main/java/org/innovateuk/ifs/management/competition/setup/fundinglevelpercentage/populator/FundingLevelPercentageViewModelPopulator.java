package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.viewmodel.FundingLevelPercentageViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Service
public class FundingLevelPercentageViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Value("${ifs.subsidy.control.northern.ireland.enabled}")
    private boolean northernIrelandSubsidyControlToggle;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competition) {
        List<ResearchCategoryResource> allResearchCategories = categoryRestService.getResearchCategories().getSuccess();

        boolean dualFunding = dualFunding(competition);

        return new FundingLevelPercentageViewModel(generalViewModel,
                allResearchCategories.stream().filter(cat -> competition.getResearchCategories().contains(cat.getId())).collect(Collectors.toList()),
                asList(OrganisationSize.values()),
                dualFunding);
    }

    private boolean dualFunding(CompetitionResource competition) {
        boolean dualFunding = competition.getFundingRules() == FundingRules.SUBSIDY_CONTROL && northernIrelandSubsidyControlToggle;

        if (dualFunding) {
            List<GrantClaimMaximumResource> maximums;
            try {
                maximums = grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId()).getSuccess();
            } catch (ObjectNotFoundException e) {
                return true;
            }
            return maximums.stream().anyMatch(m -> m.getFundingRules() != null);
        }
        return false;
    }
}
