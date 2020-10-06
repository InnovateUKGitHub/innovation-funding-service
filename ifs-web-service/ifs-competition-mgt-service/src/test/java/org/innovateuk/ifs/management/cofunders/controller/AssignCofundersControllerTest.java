package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.cofunders.populator.AssignCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.AssignCofundersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssignCofundersControllerTest extends BaseControllerMockMVCTest<AssignCofundersController> {

    @Mock
    private AssignCofundersViewModelPopulator assignCofundersViewModelPopulator;

    @Mock
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Override
    protected AssignCofundersController supplyControllerUnderTest() {
        return new AssignCofundersController();
    }

    @Test
    public void assignCofundersPage() throws Exception {
        long competitionId = 4L;
        long applicationId = 7L;
        String filter = "w";
        int page = 1;

        ApplicationResource application = newApplicationResource().withInnovationArea(newInnovationAreaResource().build()).build();
        AssignCofundersViewModel model = new AssignCofundersViewModel(new CompetitionResource(), application, null, null, Collections.emptyList());
        when(assignCofundersViewModelPopulator.populateModel(competitionId, applicationId, filter, page)).thenReturn(model);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}/cofunders/assign/{applicationId}", competitionId, applicationId)
                .param("filter", filter)
                .param("page", Integer.toString(page)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cofunders/assign"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", model))
                .andReturn();
    }
}
