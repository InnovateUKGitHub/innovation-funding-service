package com.worth.ifs.competition;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
        mockMvc.perform(get("/competition/20/details/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("competitionId", 20L));
    }
}