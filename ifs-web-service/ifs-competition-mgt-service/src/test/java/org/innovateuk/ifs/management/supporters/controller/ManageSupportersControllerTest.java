package org.innovateuk.ifs.management.supporters.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.supporters.populator.ManageSupportersViewModelPopulator;
import org.innovateuk.ifs.management.supporters.viewmodel.ManageSupportersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ManageSupportersControllerTest extends BaseControllerMockMVCTest<ManageSupportersController> {

    @Mock
    private ManageSupportersViewModelPopulator manageSupportersViewModelPopulator;

    @Override
    protected ManageSupportersController supplyControllerUnderTest() {
        return new ManageSupportersController();
    }

    @Test
    public void manageSupportersPage() throws Exception {
        long competitionId = 4L;

        ManageSupportersViewModel model = new ManageSupportersViewModel(new CompetitionResource(), true);
        when(manageSupportersViewModelPopulator.populateModel(competitionId)).thenReturn(model);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}/supporters", competitionId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("supporters/manage"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", model))
                .andReturn();
    }
}
