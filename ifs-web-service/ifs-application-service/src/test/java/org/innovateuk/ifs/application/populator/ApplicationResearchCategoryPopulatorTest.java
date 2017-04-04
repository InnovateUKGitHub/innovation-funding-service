package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationResearchCategoryPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationResearchCategoryPopulator populator;

    @Test
    public void populateWithApplicationFinances() throws Exception {

        Long questionId = 1L;
        Long applicationId = 2L;
        String competitionName = "COMP_NAME";
        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource().withApplication(applicationId).withOrganisationSize(1L).build(3);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(newApplicationResource().withCompetitionName(competitionName).build()));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(3)));
        when(financeService.getApplicationFinanceDetails(applicationId)).thenReturn(applicationFinanceResource);

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationId, questionId);

        assertEquals(questionId, researchCategoryViewModel.getQuestionId());
        assertEquals(applicationId, researchCategoryViewModel.getApplicationId());
        assertEquals(researchCategoryViewModel.getCurrentCompetitionName(), competitionName);
        assertEquals(researchCategoryViewModel.getAvailableResearchCategories().size(), 3L);
        assertEquals(researchCategoryViewModel.getHasApplicationFinances(), true);
    }

    @Test
    public void populateWithoutApplicationFinances() throws Exception {

        Long questionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;
        String competitionName = "COMP_NAME";
        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource().withApplication(applicationId).withOrganisationSize(null).build(3);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(newApplicationResource().withCompetitionName(competitionName).build()));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(3)));
        when(financeService.getApplicationFinanceDetails(applicationId)).thenReturn(applicationFinanceResource);

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationId, questionId);

        assertEquals(questionId, researchCategoryViewModel.getQuestionId());
        assertEquals(applicationId, researchCategoryViewModel.getApplicationId());
        assertEquals(researchCategoryViewModel.getCurrentCompetitionName(), competitionName);
        assertEquals(researchCategoryViewModel.getAvailableResearchCategories().size(), 3L);
        assertEquals(researchCategoryViewModel.getHasApplicationFinances(), false);
    }
}