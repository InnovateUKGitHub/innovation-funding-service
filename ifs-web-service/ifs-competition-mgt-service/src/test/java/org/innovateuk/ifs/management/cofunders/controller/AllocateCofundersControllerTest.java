package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.cofunders.populator.AllocateCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.AllocateCofundersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AllocateCofundersControllerTest extends BaseControllerMockMVCTest<AllocateCofundersController> {

    @Mock
    private AllocateCofundersViewModelPopulator allocateCofundersViewModelPopulator;

    @Override
    protected AllocateCofundersController supplyControllerUnderTest() {
        return new AllocateCofundersController();
    }

    @Test
    public void allocateCofundersPage() throws Exception {
        long competitionId = 4L;
        String filter = "w";
        int page = 1;

        AllocateCofundersViewModel model = new AllocateCofundersViewModel(new CompetitionResource(), null, null);
        when(allocateCofundersViewModelPopulator.populateModel(competitionId, filter, page)).thenReturn(model);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}/cofunders/allocate", competitionId)
                    .param("filter", filter)
                    .param("page", Integer.toString(page)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cofunders/allocate"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", model))
                .andReturn();
    }
}
