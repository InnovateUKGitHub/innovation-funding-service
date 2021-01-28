package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FundingLevelPercentageFormPopulatorTest {

    @InjectMocks
    private FundingLevelPercentageFormPopulator populator;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Test
    public void populateSingle_withoutFundingRules() {
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(Collections.emptySet())
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(restSuccess(newGrantClaimMaximumResource().withMaximum(50).build(1)));
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) populator.populateForm(competition);

        assertEquals(1, form.getMaximums().size());
        assertEquals(1, form.getMaximums().get(0).size());

        FundingLevelMaximumForm maximumForm = form.getMaximums().get(0).get(0);
        assertEquals(50, (int) maximumForm.getMaximum());
    }

    @Test
    public void populateTable_withoutFundingRules() {
        ResearchCategoryResource reseachCategory = newResearchCategoryResource().build();
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(newHashSet(reseachCategory.getId()))
                .build();


        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(restSuccess(
                newGrantClaimMaximumResource()
                .withId(1L, 2L, 3L)
                .withResearchCategory(reseachCategory)
                .withOrganisationSize(OrganisationSize.SMALL, OrganisationSize.MEDIUM, OrganisationSize.LARGE)
                .withMaximum(10, 20, 30)
                .build(3)));
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) populator.populateForm(competition);

        assertEquals(3, form.getMaximums().size());

        assertEquals(1, form.getMaximums().get(0).size());
        FundingLevelMaximumForm smallForm = form.getMaximums().get(0).get(0);
        assertEquals(10, (int) smallForm.getMaximum());

        assertEquals(1, form.getMaximums().get(1).size());
        FundingLevelMaximumForm mediumForm = form.getMaximums().get(1).get(0);
        assertEquals(20, (int) mediumForm.getMaximum());

        assertEquals(1, form.getMaximums().get(2).size());
        FundingLevelMaximumForm largeForm = form.getMaximums().get(2).get(0);
        assertEquals(30, (int) largeForm.getMaximum());
    }

    @Test
    public void populateSingle_withFundingRules() {
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(Collections.emptySet())
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(restSuccess(newGrantClaimMaximumResource().withMaximum(30, 40).withFundingRules(FundingRules.SUBSIDY_CONTROL, FundingRules.STATE_AID).build(6)));
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) populator.populateForm(competition, FundingRules.SUBSIDY_CONTROL);

        assertEquals(1, form.getMaximums().size());
        assertEquals(1, form.getMaximums().get(0).size());

        FundingLevelMaximumForm maximumForm = form.getMaximums().get(0).get(0);
        assertEquals(30, (int) maximumForm.getMaximum());
    }

    @Test
    public void populateSingle_withFundingRules_Readonly() {
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(Collections.emptySet())
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(restSuccess(newGrantClaimMaximumResource().withMaximum(30, 40).withFundingRules(FundingRules.SUBSIDY_CONTROL, FundingRules.STATE_AID).build(6)));
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) populator.populateForm(competition);

        assertEquals(1, form.getMaximums().size());
        assertEquals(2, form.getMaximums().get(0).size());

        FundingLevelMaximumForm subsidyForm = form.getMaximums().get(0).get(0);
        assertEquals(30, (int) subsidyForm.getMaximum());
        assertEquals(FundingRules.SUBSIDY_CONTROL, subsidyForm.getFundingRules());

        FundingLevelMaximumForm stateAidForm = form.getMaximums().get(0).get(1);
        assertEquals(40, (int) stateAidForm.getMaximum());
        assertEquals(FundingRules.STATE_AID, stateAidForm.getFundingRules());
    }

    @Test
    public void populateTable_withFundingRules() {
        ResearchCategoryResource reseachCategory = newResearchCategoryResource().build();
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(newHashSet(reseachCategory.getId()))
                .build();


        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(restSuccess(
                newGrantClaimMaximumResource()
                        .withId(1L, 2L, 3L, 4L, 5L, 6L)
                        .withResearchCategory(reseachCategory)
                        .withOrganisationSize(OrganisationSize.SMALL, OrganisationSize.MEDIUM, OrganisationSize.LARGE, OrganisationSize.SMALL, OrganisationSize.MEDIUM, OrganisationSize.LARGE)
                        .withMaximum(10, 20, 30, 40, 50, 60)
                        .withFundingRules(FundingRules.STATE_AID, FundingRules.STATE_AID, FundingRules.STATE_AID, FundingRules.SUBSIDY_CONTROL, FundingRules.SUBSIDY_CONTROL, FundingRules.SUBSIDY_CONTROL)
                        .build(6)));
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) populator.populateForm(competition, FundingRules.SUBSIDY_CONTROL);

        assertEquals(3, form.getMaximums().size());

        assertEquals(1, form.getMaximums().get(0).size());
        FundingLevelMaximumForm smallForm = form.getMaximums().get(0).get(0);
        assertEquals(4L, (long) smallForm.getGrantClaimMaximumId());
        assertEquals(FundingRules.SUBSIDY_CONTROL, smallForm.getFundingRules());
        assertEquals(OrganisationSize.SMALL, smallForm.getOrganisationSize());
        assertEquals(40, (int) smallForm.getMaximum());

        assertEquals(1, form.getMaximums().get(1).size());
        FundingLevelMaximumForm mediumForm = form.getMaximums().get(1).get(0);
        assertEquals(5L, (long) mediumForm.getGrantClaimMaximumId());
        assertEquals(FundingRules.SUBSIDY_CONTROL, mediumForm.getFundingRules());
        assertEquals(OrganisationSize.MEDIUM, mediumForm.getOrganisationSize());
        assertEquals(50, (int) mediumForm.getMaximum());

        assertEquals(1, form.getMaximums().get(2).size());
        FundingLevelMaximumForm largeForm = form.getMaximums().get(2).get(0);
        assertEquals(6L, (long) largeForm.getGrantClaimMaximumId());
        assertEquals(FundingRules.SUBSIDY_CONTROL, largeForm.getFundingRules());
        assertEquals(OrganisationSize.LARGE, largeForm.getOrganisationSize());
        assertEquals(60, (int) largeForm.getMaximum());
    }
    @Test
    public void populateTable_withFundingRules_Readonly() {
        ResearchCategoryResource reseachCategory = newResearchCategoryResource().build();
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(newHashSet(reseachCategory.getId()))
                .build();


        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(restSuccess(
                newGrantClaimMaximumResource()
                        .withId(1L, 2L, 3L, 4L, 5L, 6L)
                        .withResearchCategory(reseachCategory)
                        .withOrganisationSize(OrganisationSize.SMALL, OrganisationSize.MEDIUM, OrganisationSize.LARGE, OrganisationSize.SMALL, OrganisationSize.MEDIUM, OrganisationSize.LARGE)
                        .withMaximum(10, 20, 30, 40, 50, 60)
                        .withFundingRules(FundingRules.STATE_AID, FundingRules.STATE_AID, FundingRules.STATE_AID, FundingRules.SUBSIDY_CONTROL, FundingRules.SUBSIDY_CONTROL, FundingRules.SUBSIDY_CONTROL)
                        .build(6)));
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) populator.populateForm(competition);

        assertEquals(3, form.getMaximums().size());

        assertEquals(2, form.getMaximums().get(0).size());
        FundingLevelMaximumForm smallStateAid = form.getMaximums().get(0).get(0);
        assertEquals(1L, (long) smallStateAid.getGrantClaimMaximumId());
        assertEquals(FundingRules.STATE_AID, smallStateAid.getFundingRules());
        assertEquals(OrganisationSize.SMALL, smallStateAid.getOrganisationSize());
        assertEquals(10, (int) smallStateAid.getMaximum());

        FundingLevelMaximumForm smallSubsidyControl = form.getMaximums().get(0).get(1);
        assertEquals(4L, (long) smallSubsidyControl.getGrantClaimMaximumId());
        assertEquals(FundingRules.SUBSIDY_CONTROL, smallSubsidyControl.getFundingRules());
        assertEquals(OrganisationSize.SMALL, smallSubsidyControl.getOrganisationSize());
        assertEquals(40, (int) smallSubsidyControl.getMaximum());

        assertEquals(2, form.getMaximums().get(1).size());
        FundingLevelMaximumForm mediumStateAid = form.getMaximums().get(1).get(0);
        assertEquals(2L, (long) mediumStateAid.getGrantClaimMaximumId());
        assertEquals(FundingRules.STATE_AID, mediumStateAid.getFundingRules());
        assertEquals(OrganisationSize.MEDIUM, mediumStateAid.getOrganisationSize());
        assertEquals(20, (int) mediumStateAid.getMaximum());

        FundingLevelMaximumForm mediumSubsidyControl = form.getMaximums().get(1).get(1);
        assertEquals(5L, (long) mediumSubsidyControl.getGrantClaimMaximumId());
        assertEquals(FundingRules.SUBSIDY_CONTROL, mediumSubsidyControl.getFundingRules());
        assertEquals(OrganisationSize.MEDIUM, mediumSubsidyControl.getOrganisationSize());
        assertEquals(50, (int) mediumSubsidyControl.getMaximum());

        assertEquals(2, form.getMaximums().get(2).size());
        FundingLevelMaximumForm largeStateAid = form.getMaximums().get(2).get(0);
        assertEquals(3L, (long) largeStateAid.getGrantClaimMaximumId());
        assertEquals(FundingRules.STATE_AID, largeStateAid.getFundingRules());
        assertEquals(OrganisationSize.LARGE, largeStateAid.getOrganisationSize());
        assertEquals(30, (int) largeStateAid.getMaximum());

        FundingLevelMaximumForm largeSubsidyControl = form.getMaximums().get(2).get(1);
        assertEquals(6L, (long) largeSubsidyControl.getGrantClaimMaximumId());
        assertEquals(FundingRules.SUBSIDY_CONTROL, largeSubsidyControl.getFundingRules());
        assertEquals(OrganisationSize.LARGE, largeSubsidyControl.getOrganisationSize());
        assertEquals(60, (int) largeSubsidyControl.getMaximum());
    }
}
