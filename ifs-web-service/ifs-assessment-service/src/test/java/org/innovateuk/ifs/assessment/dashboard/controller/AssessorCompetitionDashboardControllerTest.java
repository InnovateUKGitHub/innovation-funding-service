package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.dashboard.form.AssessorCompetitionDashboardAssessmentForm;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionDashboardModelPopulator;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
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
    private AssessmentService assessmentService;

    @Mock
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Override
    protected AssessorCompetitionDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionDashboardController();
    }

    private CompetitionResource competitionResource;
    private AssessorCompetitionDashboardViewModel viewModel;

    @Before
    public void setup() {
        competitionResource = newCompetitionResource()
                .withId(1L)
                .withName("Juggling Craziness")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Innovation Lead")
                .withAssessorAcceptsDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .build();

        viewModel = mock(AssessorCompetitionDashboardViewModel.class);
    }

    @Test
    public void competitionDashboard() throws Exception {

        when(assessorCompetitionDashboardModelPopulator.populateModel(anyLong(), anyLong())).thenReturn(viewModel);

        mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Test
    public void submitAssessments_success() throws Exception {
        when(assessorCompetitionDashboardModelPopulator.populateModel(anyLong(), anyLong())).thenReturn(viewModel);

        mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"));
    }

    @Test
    public void submitAssessments_failure() throws Exception {

        List<Long> assessmentIds = asList(1L, 2L);

        when(assessmentService.submitAssessments(assessmentIds)).thenReturn(serviceFailure(new Error("Test Error", null)));
        when(assessorCompetitionDashboardModelPopulator.populateModel(anyLong(), anyLong())).thenReturn(viewModel);

        MvcResult result = mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competitionResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessmentIds[0]", "1")
                .param("assessmentIds[1]", "2"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        AssessorCompetitionDashboardAssessmentForm form = (AssessorCompetitionDashboardAssessmentForm) requireNonNull(result.getModelAndView()).getModel().get("form");
        assertEquals(assessmentIds, form.getAssessmentIds());

        verify(assessmentService, times(1)).submitAssessments(assessmentIds);

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getErrorCount());
        assertEquals(0, bindingResult.getFieldErrorCount());
        assertEquals("Test Error", bindingResult.getGlobalError().getCode());
    }
}
