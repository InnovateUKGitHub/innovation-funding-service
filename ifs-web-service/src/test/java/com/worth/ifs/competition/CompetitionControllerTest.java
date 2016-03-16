package com.worth.ifs.competition;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerTest extends BaseUnitTest {
    @InjectMocks
    private CompetitionController competitionController;

    @Before
    public void setUp() {
        super.setup();
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(competitionController)
                .setViewResolvers(viewResolver())
                .setHandlerExceptionResolvers(createExceptionResolver())
                .build();
    }

    @Test
    public void testCompetitionDetailsCompetitionId() throws Exception {
        User user = new User(1L, "testname", null, null, null, null, null);
        loginUser(user);

        Long compId = 20L;

        CompetitionResource competition = newCompetitionResource().with(target -> setField("id", compId, target)).build();
        when(competitionService.getById(compId)).thenReturn(competition);

        mockMvc.perform(get("/competition/{id}/details/", compId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCompetition", competition));
    }
}