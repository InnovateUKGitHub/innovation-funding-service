package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationResearchCategoryPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationResearchCategoryPopulator populator;

    @Test
    public void populate() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        String competitionName = "COMP_NAME";

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(newApplicationResource().withCompetitionName(competitionName).build()));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(3)));

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationId, questionId);

        assertEquals(questionId, researchCategoryViewModel.getQuestionId());
        assertEquals(applicationId, researchCategoryViewModel.getApplicationId());
        assertEquals(researchCategoryViewModel.getCurrentCompetitionName(), competitionName);
        assertEquals(researchCategoryViewModel.getAvailableResearchCategories().size(), 3L);
    }
}