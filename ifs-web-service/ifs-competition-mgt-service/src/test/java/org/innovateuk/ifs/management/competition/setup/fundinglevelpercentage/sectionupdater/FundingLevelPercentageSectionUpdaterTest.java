package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.sectionupdater;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FundingLevelPercentageSectionUpdaterTest {

    @InjectMocks
    private FundingLevelPercentageSectionUpdater updater;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Mock
    private CategoryRestService categoryRestService;

    @Test
    public void update_single() {
        CompetitionResource competition = newCompetitionResource().build();
        FundingLevelMaximumForm maximum = new FundingLevelMaximumForm();
        maximum.setMaximum(100);
        FundingLevelPercentageForm form = new FundingLevelPercentageForm();
        form.setMaximums(singletonList(singletonList(maximum)));

        GrantClaimMaximumResource grantClaimMaximumResource1 = newGrantClaimMaximumResource().build();
        GrantClaimMaximumResource grantClaimMaximumResource2 = newGrantClaimMaximumResource().build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(
                restSuccess(asList(grantClaimMaximumResource1, grantClaimMaximumResource2))
        );
        when(grantClaimMaximumRestService.save(grantClaimMaximumResource1)).thenReturn(restSuccess(grantClaimMaximumResource1));
        when(grantClaimMaximumRestService.save(grantClaimMaximumResource2)).thenReturn(restSuccess(grantClaimMaximumResource2));

        ServiceResult<Void> result = updater.doSaveSection(competition, form);

        assertTrue(result.isSuccess());
        assertEquals(100, (int) grantClaimMaximumResource1.getMaximum());
        assertEquals(100, (int) grantClaimMaximumResource2.getMaximum());

        verify(grantClaimMaximumRestService).save(grantClaimMaximumResource1);
        verify(grantClaimMaximumRestService).save(grantClaimMaximumResource2);
    }

    @Test
    public void update_single_fundingRules() {
        CompetitionResource competition = newCompetitionResource().build();
        FundingLevelMaximumForm maximum = new FundingLevelMaximumForm();
        maximum.setMaximum(100);
        maximum.setFundingRules(FundingRules.STATE_AID);
        FundingLevelPercentageForm form = new FundingLevelPercentageForm();
        form.setMaximums(singletonList(singletonList(maximum)));

        GrantClaimMaximumResource grantClaimMaximumResource1 = newGrantClaimMaximumResource().withFundingRules(FundingRules.STATE_AID).build();
        GrantClaimMaximumResource grantClaimMaximumResource2 = newGrantClaimMaximumResource().withFundingRules(FundingRules.SUBSIDY_CONTROL).build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(
                restSuccess(asList(grantClaimMaximumResource1, grantClaimMaximumResource2))
        );
        when(grantClaimMaximumRestService.save(grantClaimMaximumResource1)).thenReturn(restSuccess(grantClaimMaximumResource1));

        ServiceResult<Void> result = updater.doSaveSection(competition, form);

        assertTrue(result.isSuccess());
        assertEquals(100, (int) grantClaimMaximumResource1.getMaximum());

        verify(grantClaimMaximumRestService).save(grantClaimMaximumResource1);
        verify(grantClaimMaximumRestService, never()).save(grantClaimMaximumResource2);
    }

    @Test
    public void update_table() {
        ResearchCategoryResource matchingResearchCategory = newResearchCategoryResource().build();
        ResearchCategoryResource missingResearchCategory = newResearchCategoryResource().build();

        FundingLevelPercentageForm form = new FundingLevelPercentageForm();
        form.setMaximums(
                asList(
                        newArrayList(new FundingLevelMaximumForm(100L, matchingResearchCategory.getId(), OrganisationSize.SMALL, null, 10)),
                        newArrayList(new FundingLevelMaximumForm(101L, matchingResearchCategory.getId(), OrganisationSize.MEDIUM, null,  20)),
                        newArrayList(new FundingLevelMaximumForm(102L, matchingResearchCategory.getId(), OrganisationSize.LARGE, null,  30))
                )
        );
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(newHashSet(matchingResearchCategory.getId()))
                .build();

        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(newArrayList(matchingResearchCategory, missingResearchCategory)));
        when(grantClaimMaximumRestService.save(any())).thenReturn(restSuccess(new GrantClaimMaximumResource()));

        GrantClaimMaximumResource missingGrantClaimMaxmimum = newGrantClaimMaximumResource()
                .withResearchCategory(missingResearchCategory)
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(
                restSuccess(newArrayList(missingGrantClaimMaxmimum))
        );

        ServiceResult<Void> result = updater.doSaveSection(competition, form);

        assertTrue(result.isSuccess());

        verify(grantClaimMaximumRestService).save(argThat(lambdaMatches(max -> max.getId().equals(100L)
                && max.getResearchCategory().equals(matchingResearchCategory)
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum().equals(10)
        )));
        verify(grantClaimMaximumRestService).save(argThat(lambdaMatches(max -> max.getId().equals(101L)
                && max.getResearchCategory().equals(matchingResearchCategory)
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum().equals(20)
        )));
        verify(grantClaimMaximumRestService).save(argThat(lambdaMatches(max -> max.getId().equals(102L)
                && max.getResearchCategory().equals(matchingResearchCategory)
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum().equals(30)
        )));

        verify(grantClaimMaximumRestService).save(missingGrantClaimMaxmimum);
        assertEquals(0, (int) missingGrantClaimMaxmimum.getMaximum());
    }

    @Test
    public void update_table_fundingRules() {
        ResearchCategoryResource matchingResearchCategory = newResearchCategoryResource().build();
        ResearchCategoryResource missingResearchCategory = newResearchCategoryResource().build();

        FundingLevelPercentageForm form = new FundingLevelPercentageForm();
        form.setMaximums(
                asList(
                        newArrayList(new FundingLevelMaximumForm(100L, matchingResearchCategory.getId(), OrganisationSize.SMALL, FundingRules.STATE_AID, 10)),
                        newArrayList(new FundingLevelMaximumForm(101L, matchingResearchCategory.getId(), OrganisationSize.MEDIUM, FundingRules.STATE_AID,  20)),
                        newArrayList(new FundingLevelMaximumForm(102L, matchingResearchCategory.getId(), OrganisationSize.LARGE, FundingRules.STATE_AID,  30))
                )
        );
        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(newHashSet(matchingResearchCategory.getId()))
                .build();

        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(newArrayList(matchingResearchCategory, missingResearchCategory)));
        when(grantClaimMaximumRestService.save(any())).thenReturn(restSuccess(new GrantClaimMaximumResource()));

        GrantClaimMaximumResource missingGrantClaimMaxmimum = newGrantClaimMaximumResource()
                .withResearchCategory(missingResearchCategory)
                .withFundingRules(FundingRules.STATE_AID)
                .build();
        GrantClaimMaximumResource missingGrantClaimMaxmimumMismatchFundingRule = newGrantClaimMaximumResource()
                .withResearchCategory(missingResearchCategory)
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId())).thenReturn(
                restSuccess(newArrayList(missingGrantClaimMaxmimum, missingGrantClaimMaxmimumMismatchFundingRule))
        );

        ServiceResult<Void> result = updater.doSaveSection(competition, form);

        assertTrue(result.isSuccess());

        verify(grantClaimMaximumRestService).save(argThat(lambdaMatches(max -> max.getId().equals(100L)
                && max.getResearchCategory().equals(matchingResearchCategory)
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum().equals(10)
        )));
        verify(grantClaimMaximumRestService).save(argThat(lambdaMatches(max -> max.getId().equals(101L)
                && max.getResearchCategory().equals(matchingResearchCategory)
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum().equals(20)
        )));
        verify(grantClaimMaximumRestService).save(argThat(lambdaMatches(max -> max.getId().equals(102L)
                && max.getResearchCategory().equals(matchingResearchCategory)
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum().equals(30)
        )));

        verify(grantClaimMaximumRestService).save(missingGrantClaimMaxmimum);
        assertEquals(0, (int) missingGrantClaimMaxmimum.getMaximum());
        verify(grantClaimMaximumRestService, never()).save(missingGrantClaimMaxmimumMismatchFundingRule);
    }
}
