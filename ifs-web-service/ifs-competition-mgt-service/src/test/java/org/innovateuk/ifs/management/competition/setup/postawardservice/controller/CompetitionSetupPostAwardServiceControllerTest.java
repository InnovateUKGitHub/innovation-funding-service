package org.innovateuk.ifs.management.competition.setup.postawardservice.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.builder.CompetitionPostAwardServiceResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupPostAwardServiceRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.postawardservice.form.PostAwardServiceForm;
import org.innovateuk.ifs.management.competition.setup.postawardservice.populator.ChoosePostAwardServiceModelPopulator;
import org.innovateuk.ifs.management.competition.setup.postawardservice.viewmodel.ChoosePostAwardServiceViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupPostAwardServiceControllerTest extends BaseControllerMockMVCTest<CompetitionSetupPostAwardServiceController> {
    private static final long COMPETITION_ID = 12L;
    private static final String COMPETITION_NAME = "competitionName";

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private ChoosePostAwardServiceModelPopulator choosePostAwardServiceModelPopulator;

    @Mock
    private CompetitionSetupPostAwardServiceRestService competitionSetupPostAwardServiceRestService;

    @Override
    protected CompetitionSetupPostAwardServiceController supplyControllerUnderTest() {
        return new CompetitionSetupPostAwardServiceController();
    }

    @Before
    public void setUp() {
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
    }

    @Test
    public void managePostAwardService() throws Exception {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(COMPETITION_NAME)
                .build();

        CompetitionPostAwardServiceResource competitionPostAwardServiceResource = CompetitionPostAwardServiceResourceBuilder.newCompetitionPostAwardServiceResource()
                .withCompetitionId(COMPETITION_ID)
                .withPostAwardService(PostAwardService.CONNECT)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupPostAwardServiceRestService.getPostAwardService(COMPETITION_ID)).thenReturn(restSuccess(competitionPostAwardServiceResource));

        ChoosePostAwardServiceViewModel viewModel = new ChoosePostAwardServiceViewModel(COMPETITION_ID, COMPETITION_NAME);
        when(choosePostAwardServiceModelPopulator.populateModel(competitionResource)).thenReturn(viewModel);

        PostAwardServiceForm expectedForm = new PostAwardServiceForm();
        expectedForm.setPostAwardService(PostAwardService.CONNECT);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/setup/{competitionId}/post-award-service", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/post-award-service"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("form", expectedForm));
    }

    @Test
    public void choosePostAwardService() throws Exception {

        when(competitionSetupPostAwardServiceRestService.setPostAwardService(eq(COMPETITION_ID), any())).thenReturn(restSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/competition/setup/{competitionId}/post-award-service", COMPETITION_ID)
                .param("postAwardService", "CONNECT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupPostAwardServiceRestService).setPostAwardService(eq(COMPETITION_ID), any());
    }

    @Test
    public void choosePostAwardServiceWithNoneSelected() throws Exception {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(COMPETITION_NAME)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        ChoosePostAwardServiceViewModel viewModel = new ChoosePostAwardServiceViewModel(COMPETITION_ID, COMPETITION_NAME);
        when(choosePostAwardServiceModelPopulator.populateModel(competitionResource)).thenReturn(viewModel);

        PostAwardServiceForm expectedForm = new PostAwardServiceForm();

        mockMvc.perform(MockMvcRequestBuilders.post("/competition/setup/{competitionId}/post-award-service", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/post-award-service"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("form", expectedForm));

        verifyZeroInteractions(competitionSetupPostAwardServiceRestService);
    }
}
