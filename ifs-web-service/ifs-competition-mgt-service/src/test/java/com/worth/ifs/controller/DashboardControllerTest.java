
package com.worth.ifs.controller;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import com.worth.ifs.competition.controller.DashboardController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.service.CompetitionService;

/**
 * Class for testing public functions of {@link DashboardController}
 */
@RunWith(MockitoJUnitRunner.class)
public class DashboardControllerTest {

    private static final Long COMPETITION_ID = Long.valueOf(12L);

    @InjectMocks
	private DashboardController controller;
	
    @Mock
    private CompetitionService competitionService;

    private MockMvc mockMvc;


    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void showingDashboard() throws Exception {

        List competitions = asList(newCompetitionResource().withId(COMPETITION_ID));

        when(competitionService.getAllCompetitions()).thenReturn(competitions);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/list"))
                .andExpect(model().attribute("competitions", is(competitions)));
    }
}