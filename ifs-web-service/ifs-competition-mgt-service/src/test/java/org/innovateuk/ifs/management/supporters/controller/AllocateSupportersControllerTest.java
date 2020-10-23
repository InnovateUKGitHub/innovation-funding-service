package org.innovateuk.ifs.management.supporters.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.supporters.populator.AllocateSupportersViewModelPopulator;
import org.innovateuk.ifs.management.supporters.viewmodel.AllocateSupportersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AllocateSupportersControllerTest extends BaseControllerMockMVCTest<AllocateSupportersController> {

    @Mock
    private AllocateSupportersViewModelPopulator allocateSupportersViewModelPopulator;

    @Override
    protected AllocateSupportersController supplyControllerUnderTest() {
        return new AllocateSupportersController();
    }

    @Test
    public void allocateSupportersPage() throws Exception {
        long competitionId = 4L;
        String filter = "w";
        int page = 1;

        AllocateSupportersViewModel model = new AllocateSupportersViewModel(new CompetitionResource(), null, null);
        when(allocateSupportersViewModelPopulator.populateModel(competitionId, filter, page)).thenReturn(model);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}/supporters/allocate", competitionId)
                    .param("filter", filter)
                    .param("page", Integer.toString(page)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("supporters/allocate"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", model))
                .andReturn();
    }
}
