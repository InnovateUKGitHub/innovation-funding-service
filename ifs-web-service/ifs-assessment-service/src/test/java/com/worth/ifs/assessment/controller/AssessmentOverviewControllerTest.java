package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static com.worth.ifs.application.service.Futures.settable;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentOverviewControllerTest  extends BaseControllerMockMVCTest<AssessmentOverviewController> {

    @InjectMocks
    private AssessmentOverviewController assessmentOverviewController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private AssessmentService assessmentService;

    @Spy
    @InjectMocks
    private AssessmentOverviewModelPopulator applicationOverviewModelPopulator;

    @Override
    protected AssessmentOverviewController supplyControllerUnderTest() {
        return new AssessmentOverviewController();
    }

    @Before
    public void setUp(){
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(Optional.ofNullable(organisations.get(0)));
    }

    @Test
    public void testAssessmentDetails() throws Exception {
        AssessmentResource assessment = new AssessmentResource();
        assessment.setId(0L);

        ProcessRoleResource processRole = new ProcessRoleResource();
        processRole.setId(0L);
        processRole.setApplication(1L);

        CompetitionResource competition = new CompetitionResource();
        competition.setId(1L);

        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L,2L);
        Map<Long, Set<Long>> mappedSections = new HashMap();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(competitionService.getById(app.getCompetition())).thenReturn(competition);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        // when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(processRoleService.getById(assessment.getId())).thenReturn(settable(processRole));

        LOG.debug("Show assessment overview: " + assessment.getId());
        mockMvc.perform(get("/assessment/" + assessment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-application-overview"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("completedSections", sections))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())));
    }
}
