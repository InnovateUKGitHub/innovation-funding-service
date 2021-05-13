package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.viewmodel.FundingLevelPercentageViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FundingLevelPercentageViewModelPopulatorTest {

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @InjectMocks
    private FundingLevelPercentageViewModelPopulator populator;

    @Test
    public void shouldPopulateNiSubsidyToggleFalse() {
        // given
        ReflectionTestUtils.setField(populator, "northernIrelandSubsidyControlToggle", false);

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, null, null, null, false, false);
        CompetitionResource competition = newCompetitionResource()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withResearchCategories(Collections.emptySet())
                .build();

        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(2);
        given(categoryRestService.getResearchCategories()).willReturn(restSuccess(researchCategories));

        // when
        CompetitionSetupViewModel result = populator.populateModel(generalSetupViewModel, competition);

        // then
        assertThat(result).isInstanceOf(FundingLevelPercentageViewModel.class);
        FundingLevelPercentageViewModel resultAsFlpvm = (FundingLevelPercentageViewModel) result;
        assertThat(resultAsFlpvm.isDualFunding()).isFalse();
    }

    @Test
    public void shouldPopulateNiSubsidyToggleTrue() {
        // given
        ReflectionTestUtils.setField(populator, "northernIrelandSubsidyControlToggle", true);

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, null, null, null, false, false);
        CompetitionResource competition = newCompetitionResource()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withResearchCategories(Collections.emptySet())
                .build();

        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(2);
        given(categoryRestService.getResearchCategories()).willReturn(restSuccess(researchCategories));

        List<GrantClaimMaximumResource> grantClaimMaximums = newGrantClaimMaximumResource()
                .withFundingRules(FundingRules.STATE_AID, FundingRules.SUBSIDY_CONTROL)
                .build(2);
        given(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).willReturn(restSuccess(grantClaimMaximums));

        // when
        CompetitionSetupViewModel result = populator.populateModel(generalSetupViewModel, competition);

        // then
        assertThat(result).isInstanceOf(FundingLevelPercentageViewModel.class);
        FundingLevelPercentageViewModel resultAsFlpvm = (FundingLevelPercentageViewModel) result;
        assertThat(resultAsFlpvm.isDualFunding()).isTrue();
    }

    @Test
    public void shouldPopulateNiSubsidyToggleTrueButGrantClaimMaxHaveNoFundingType() {
        // given
        ReflectionTestUtils.setField(populator, "northernIrelandSubsidyControlToggle", true);

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, null, null, null, false, false);
        CompetitionResource competition = newCompetitionResource()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withResearchCategories(Collections.emptySet())
                .build();

        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(2);
        given(categoryRestService.getResearchCategories()).willReturn(restSuccess(researchCategories));

        List<GrantClaimMaximumResource> grantClaimMaximums = newGrantClaimMaximumResource()
                .build(2);
        given(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).willReturn(restSuccess(grantClaimMaximums));

        // when
        CompetitionSetupViewModel result = populator.populateModel(generalSetupViewModel, competition);

        // then
        assertThat(result).isInstanceOf(FundingLevelPercentageViewModel.class);
        FundingLevelPercentageViewModel resultAsFlpvm = (FundingLevelPercentageViewModel) result;
        assertThat(resultAsFlpvm.isDualFunding()).isFalse();
    }

    @Test
    public void shouldPopulateNiSubsidyToggleTrueButGrantClaimMaxNotFound() {
        // given
        ReflectionTestUtils.setField(populator, "northernIrelandSubsidyControlToggle", true);

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, null, null, null, false, false);
        CompetitionResource competition = newCompetitionResource()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withResearchCategories(Collections.emptySet())
                .build();

        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(2);
        given(categoryRestService.getResearchCategories()).willReturn(restSuccess(researchCategories));

        given(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).willThrow(ObjectNotFoundException.class);

        // when
        CompetitionSetupViewModel result = populator.populateModel(generalSetupViewModel, competition);

        // then
        assertThat(result).isInstanceOf(FundingLevelPercentageViewModel.class);
        FundingLevelPercentageViewModel resultAsFlpvm = (FundingLevelPercentageViewModel) result;
        assertThat(resultAsFlpvm.isDualFunding()).isTrue();
    }
}
