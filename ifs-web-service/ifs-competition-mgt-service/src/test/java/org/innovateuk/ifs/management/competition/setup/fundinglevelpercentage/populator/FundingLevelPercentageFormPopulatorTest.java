package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
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
    public void populateSingle() {
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
    public void populateTable() {
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
}
