package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionDashboardModelPopulator;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorCompetitionDashboardControllerTest extends AbstractApplicationMockMVCTest<AssessorCompetitionDashboardController> {

    @Mock
    private AssessmentService assessmentServiceMock;

    @Mock
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Override
    protected AssessorCompetitionDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionDashboardController();
    }

    @Test
    public void competitionDashboard() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(1L)
                .withName("Juggling Craziness")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Innovation Lead")
                .withAssessorAcceptsDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .build();

        AssessorCompetitionDashboardViewModel viewModel = mock(AssessorCompetitionDashboardViewModel.class);
        when(assessorCompetitionDashboardModelPopulator.populateModel(anyLong(), anyLong())).thenReturn(viewModel);

        mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Test
    public void submitAssessments_success() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(1L)
                .withName("Juggling Craziness")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Innovation Lead")
                .withAssessorAcceptsDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .build();

        List<Long> assessmentIds = asList(1L, 2L);

        AssessorCompetitionDashboardViewModel viewModel = mock(AssessorCompetitionDashboardViewModel.class);
        when(assessmentServiceMock.submitAssessments(assessmentIds)).thenReturn(serviceSuccess());
        when(assessorCompetitionDashboardModelPopulator.populateModel(anyLong(), anyLong())).thenReturn(viewModel);

        mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"));
    }

    @Test
    public void submitAssessments_failure() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(1L)
                .withName("Juggling Craziness")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Innovation Lead")
                .withAssessorAcceptsDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .build();

        List<Long> assessmentIds = asList(1L, 2L);

        AssessorCompetitionDashboardViewModel viewModel = mock(AssessorCompetitionDashboardViewModel.class);

        when(assessmentServiceMock.submitAssessments(assessmentIds)).thenReturn(serviceFailure(new Error("Test Error", null)));
        when(assessorCompetitionDashboardModelPopulator.populateModel(anyLong(), anyLong())).thenReturn(viewModel);

        MvcResult result = mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competitionResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeHasFieldErrors("form", "assessmentIds"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        verify(assessmentServiceMock, times(1)).submitAssessments(assessmentIds);
        verifyNoMoreInteractions(assessmentServiceMock);

    }
}
