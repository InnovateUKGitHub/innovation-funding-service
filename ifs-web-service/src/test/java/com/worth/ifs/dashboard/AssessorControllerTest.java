package com.worth.ifs.dashboard;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorControllerTest  extends BaseUnitTest {

    @InjectMocks
    private AssessorController assessorController;

    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assessorController)
                .setViewResolvers(viewResolver())
                .build();


        this.setupCompetition();
        this.setupUserRoles();
        this.setupApplicationWithRoles();
        this.setupAssessment();
    }


    @Test
     public void testDashboardWithAssessorLogin() throws Exception {
        this.loginUser(assessor);

        List<Competition> competitions = new ArrayList<>();
        competitions.add(competition);

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        competitionsTotalAssignedAssessments.put(competition.getId(), 3);

        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();
        competitionsSubmittedAssessments.put(competition.getId(), 1);

        mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("assessor-dashboard"))
                .andExpect(model().attribute("competitionsForAssessment", competitions))
                .andExpect(model().attribute("totalAssignedAssessments", competitionsTotalAssignedAssessments))
                .andExpect(model().attribute("submittedAssessments", competitionsSubmittedAssessments));
    }

    @Ignore
    public void testDashboardWithApplicantLogin() throws Exception {
        this.loginUser(applicant);

        List<Competition> competitions = new ArrayList<>();
        competitions.add(competition);

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        competitionsTotalAssignedAssessments.put(competition.getId(), 3);

        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();
        competitionsSubmittedAssessments.put(competition.getId(), 1);

        mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("assessor-dashboard"))
                .andExpect(model().attribute("competitionsForAssessment", competitions))
                .andExpect(model().attribute("totalAssignedAssessments", competitionsTotalAssignedAssessments))
                .andExpect(model().attribute("submittedAssessments", competitionsSubmittedAssessments));
    }

    @Ignore
    public void testDashboardWithoutLogin() throws Exception {
        List<Competition> competitions = new ArrayList<>();
        competitions.add(competition);

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        competitionsTotalAssignedAssessments.put(competition.getId(), 3);

        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();
        competitionsSubmittedAssessments.put(competition.getId(), 1);

        mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("assessor-dashboard"))
                .andExpect(model().attribute("competitionsForAssessment", competitions))
                .andExpect(model().attribute("totalAssignedAssessments", competitionsTotalAssignedAssessments))
                .andExpect(model().attribute("submittedAssessments", competitionsSubmittedAssessments));
    }
}