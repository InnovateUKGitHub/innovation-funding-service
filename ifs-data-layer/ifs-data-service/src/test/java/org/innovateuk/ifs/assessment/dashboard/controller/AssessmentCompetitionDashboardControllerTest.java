package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.dashboard.transactional.AssessmentCompetitionDashboardService;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZoneId;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.ACCEPTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AssessmentCompetitionDashboardControllerTest extends BaseControllerMockMVCTest<AssessmentCompetitionDashboardController> {

    @Mock
    private AssessmentCompetitionDashboardService assessmentCompetitionDashboardServiceMock;

    @Override
    protected AssessmentCompetitionDashboardController supplyControllerUnderTest() {
        return new AssessmentCompetitionDashboardController();
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        User user = newUser().withId(52L).build();
        User leadTechnologist = newUser().withId(42L).withFirstName("Paul").withLastName("Plum").build();
        Competition competition = newCompetition()
                .withId(8L)
                .withName("Test Competition")
                .withLeadTechnologist(leadTechnologist)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(4).atZone(ZoneId.systemDefault()))
                .build();

        ApplicationAssessmentResource assessments = newApplicationAssessmentResource()
                .withApplicationId(1L)
                .withAssessmentId(2L)
                .withApplicationName("Test Application")
                .withLeadOrganisation("Lead Company")
                .withState(ACCEPTED)
                .build();

        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = new AssessorCompetitionDashboardResource(
                competition.getId(),
                competition.getName(),
                competition.getLeadTechnologist().getName(),
                competition.getAssessorAcceptsDate(),
                competition.getAssessorDeadlineDate(),
                singletonList(assessments)
        );

        when(assessmentCompetitionDashboardServiceMock.getAssessorCompetitionDashboardResource(user.getId(), competition.getId())).thenReturn(serviceSuccess(assessorCompetitionDashboardResource));

        mockMvc.perform(get("/assessment/user/{userId}/competition/{competitionId}/dashboard", user.getId(), competition.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessorCompetitionDashboardResource)));

        RestResult<AssessorCompetitionDashboardResource> result = assessmentCompetitionDashboardServiceMock.getAssessorCompetitionDashboardResource(user.getId(), competition.getId()).toGetResponse();

        assertTrue(result.isSuccess());
        verify(assessmentCompetitionDashboardServiceMock, times(2)).getAssessorCompetitionDashboardResource(user.getId(), competition.getId());
        verifyNoMoreInteractions(assessmentCompetitionDashboardServiceMock);
    }
}