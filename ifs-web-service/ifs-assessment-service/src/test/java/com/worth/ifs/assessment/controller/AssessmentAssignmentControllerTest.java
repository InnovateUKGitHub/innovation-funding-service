package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessmentAssignmentModelPopulator;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentAssignmentControllerTest extends BaseControllerMockMVCTest<AssessmentAssignmentController> {

    @Spy
    @InjectMocks
    private AssessmentAssignmentModelPopulator assessmentAssignmentModelPopulator;

    @Mock
    private AssessmentService assessmentService;

    private List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/assign/application/";


    @Override
    protected AssessmentAssignmentController supplyControllerUnderTest() {
        return new AssessmentAssignmentController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        when(rejectionReasonRestService.findAllActive()).thenReturn(restSuccess(rejectionReasons));
    }

    @Test
    public void viewAssignment() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        Long formInput = 11L;
        Long competitionId = 3L;

        FormInputResponseResource applicantResponse =
                newFormInputResponseResource()
                        .withFormInputs(formInput)
                        .with(idBasedValues("Value "))
                        .build();

        when(assessmentService.getById(assessmentId)).thenReturn(newAssessmentResource().withApplication(applicationId).build());
        when(competitionService.getById(competitionId)).thenReturn(newCompetitionResource().build());
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());
        when(formInputResponseService.getByFormInputIdAndApplication(formInput, applicationId)).thenReturn(restSuccess(asList(applicantResponse)));

        mockMvc.perform(get("/{assessmentId}/assignment", assessmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("assessment/assessment-invitation"));

    }

    @Test
    public void acceptAssignment() throws Exception {
        Long assessmentId = 1L;

        mockMvc.perform(post("/{assessmentId}/assignment/accept", assessmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/1/assignment/accepted"));

    }
}
