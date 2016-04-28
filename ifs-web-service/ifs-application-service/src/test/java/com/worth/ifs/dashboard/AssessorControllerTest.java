package com.worth.ifs.dashboard;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
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

import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorControllerTest  extends BaseUnitTest {

    @InjectMocks
    private AssessorController assessorController;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assessorController)
                .setViewResolvers(viewResolver())
                .build();

        super.setup();

        this.setupCompetition();
        this.setupUserRoles();
        this.setupApplicationWithRoles();
        this.setupAssessment();
    }


    @Test
     public void testDashboardWithAssessorLogin() throws Exception {
        this.loginUser(assessor);

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        competitionsTotalAssignedAssessments.put(competitionResource.getId(), 3);

        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();
        competitionsSubmittedAssessments.put(competitionResource.getId(), 1);

        when(competitionService.getAllCompetitions()).thenReturn(competitionResources);

        mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("assessor-dashboard"))
                .andExpect(model().attribute("competitionsForAssessment", competitionResources))
                .andExpect(model().attribute("totalAssignedAssessments", competitionsTotalAssignedAssessments))
                .andExpect(model().attribute("submittedAssessments", competitionsSubmittedAssessments));
    }

    @Ignore
    public void testDashboardWithApplicantLogin() throws Exception {
        this.loginUser(applicant);

        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        competitionsTotalAssignedAssessments.put(competitionResource.getId(), 3);

        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();
        competitionsSubmittedAssessments.put(competitionResource.getId(), 1);

        mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("assessor-dashboard"))
                .andExpect(model().attribute("competitionsForAssessment", competitionResources))
                .andExpect(model().attribute("totalAssignedAssessments", competitionsTotalAssignedAssessments))
                .andExpect(model().attribute("submittedAssessments", competitionsSubmittedAssessments));
    }

    @Ignore
    public void testDashboardWithoutLogin() throws Exception {
        Map<Long, Integer> competitionsTotalAssignedAssessments = new HashMap<>();
        competitionsTotalAssignedAssessments.put(competitionResource.getId(), 3);

        Map<Long, Integer> competitionsSubmittedAssessments = new HashMap<>();
        competitionsSubmittedAssessments.put(competitionResource.getId(), 1);

        mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("assessor-dashboard"))
                .andExpect(model().attribute("competitionsForAssessment", competitionResources))
                .andExpect(model().attribute("totalAssignedAssessments", competitionsTotalAssignedAssessments))
                .andExpect(model().attribute("submittedAssessments", competitionsSubmittedAssessments));
    }
}