package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.cofunders.populator.ManageCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.ManageCofundersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ManageCofundersControllerTest extends BaseControllerMockMVCTest<ManageCofundersController> {

    @Mock
    private ManageCofundersViewModelPopulator manageCofundersViewModelPopulator;

    @Override
    protected ManageCofundersController supplyControllerUnderTest() {
        return new ManageCofundersController();
    }

    @Test
    public void manageCofundersPage() throws Exception {
        long competitionId = 4L;

        ManageCofundersViewModel model = new ManageCofundersViewModel(new CompetitionResource());
        when(manageCofundersViewModelPopulator.populateModel(competitionId)).thenReturn(model);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}/cofunders", competitionId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("cofunders/manage"))
                .andExpect(MockMvcResultMatchers.model().attribute("model", model))
                .andReturn();
    }
}
