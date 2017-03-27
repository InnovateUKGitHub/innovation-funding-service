package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.InnovationAreaViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationInnovationAreaPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationInnovationAreaPopulator populator;


    @Test
    public void populate() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        String competitionName = "COMP_NAME";

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(newApplicationResource().withCompetitionName(competitionName).build()));
        when(applicationInnovationAreaRestService.getAvailableInnovationAreasForApplication(applicationId)).thenReturn(restSuccess(newInnovationAreaResource().build(5)));

        InnovationAreaViewModel innovationAreaViewModel = populator.populate(applicationId, questionId);

        assertEquals(questionId, innovationAreaViewModel.getQuestionId());
        assertEquals(applicationId, innovationAreaViewModel.getApplicationId());
        assertEquals(innovationAreaViewModel.getCurrentCompetitionName(), competitionName);
        assertEquals(innovationAreaViewModel.getAvailableInnovationAreas().size(), 5L);
    }

    @Test
    public void populate_noInnovationAreaApplicableShouldBeSetWhenAppropriate() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        String competitionName = "COMP_NAME";
        Long innovationAreaId = 3L;

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().withId(innovationAreaId).withName("Innovation Area").build();
        ApplicationResource applicationWithInnovationArea = newApplicationResource().withInnovationArea(innovationAreaResource).withCompetitionName(competitionName).build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationWithInnovationArea));
        when(applicationInnovationAreaRestService.getAvailableInnovationAreasForApplication(applicationId)).thenReturn(restSuccess(newInnovationAreaResource().build(5)));

        InnovationAreaViewModel innovationAreaViewModel = populator.populate(applicationId, questionId);

        assertEquals(innovationAreaId, innovationAreaViewModel.getSelectedInnovationAreaId());
        assertEquals(false, innovationAreaViewModel.isNoInnovationAreaApplicable());
    }

    @Test
    public void populate_innovationAreaShouldBeSetWhenInnovationAreaIsApplicableForApplication() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        String competitionName = "COMP_NAME";

        ApplicationResource applicationWithInnovationArea = newApplicationResource().withNoInnovationAreaApplicable(true).withCompetitionName(competitionName).build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationWithInnovationArea));
        when(applicationInnovationAreaRestService.getAvailableInnovationAreasForApplication(applicationId)).thenReturn(restSuccess(newInnovationAreaResource().build(5)));

        InnovationAreaViewModel innovationAreaViewModel = populator.populate(applicationId, questionId);

        assertEquals(null, innovationAreaViewModel.getSelectedInnovationAreaId());
        assertEquals(true, innovationAreaViewModel.isNoInnovationAreaApplicable());
    }
}