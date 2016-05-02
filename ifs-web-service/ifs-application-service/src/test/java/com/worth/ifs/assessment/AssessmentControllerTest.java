package com.worth.ifs.assessment;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.viewmodel.AssessmentDashboardModel;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentControllerTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentController assessmentController;

    /* pages */
    private final String assessorDashboard = "assessor-dashboard";
    private final String assessmentDetails = "assessment-details";
    private final String assessmentSubmitReview = "assessment-submit-review";
    private final String applicationReview = "application-assessment-review";
    private final String rejectInvitation = "reject-assessment-invitation";


    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(assessmentController)
                .setViewResolvers(viewResolver())
                .build();

        super.setup();

        this.setupCompetition();
        this.setupUserRoles();
        this.loginUser(assessor);
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupAssessment();
        this.setupInvites();
    }

    @Ignore
    @Test
    public void testCompetitionAssessmentDashboard() throws Exception {
        List<Assessment> nonSubmittedAssessments = assessments.stream().filter(a -> !a.isSubmitted()).collect(toList());
        nonSubmittedAssessments.sort(new AssessmentStatusComparator());

        long noOfAssessmentsStartedAwaitingSubmission = nonSubmittedAssessments.stream().filter(Assessment::hasAssessmentStarted).count();
        boolean hasAssesmentsStartedAwaitingSubmission = noOfAssessmentsStartedAwaitingSubmission > 0;

        MvcResult mvcResult = mockMvc.perform(get("/assessor/competitions/{competitionId}/applications", competitionResource.getId())).andReturn();
        AssessmentDashboardModel model = this.attributeFromMvcResultModel(mvcResult, "model");
        assertNotNull(model);
        assertEquals(3, model.getAssessments().size());
        assertEquals(nonSubmittedAssessments.get(0).getId(), model.getAssessments().get(0).getAssessment().getId());
        assertEquals(submittedAssessments.get(0).getId(), model.getSubmittedAssessments().get(0).getAssessment().getId());
        assertEquals(competitionResource.getId(), model.getCompetition().getId());
        assertEquals(noOfAssessmentsStartedAwaitingSubmission, model.getNoOfAsssessmentsStartedAwaitingSubmission());
        assertEquals(hasAssesmentsStartedAwaitingSubmission, model.hasAssesmentsForSubmission());
    }

    @Ignore
    @Test
    public void testUserIsNotAssessorOnApplication() throws Exception {

        this.loginUser(applicant);
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competitionResource.getId(), application.getId()))
                .andExpect(view().name(assessorDashboard))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetition())))
                .andExpect(model().attributeDoesNotExist("assessment"));
    }

    @Ignore
    @Test
    public void testApplicationAssessmentDetailsPendingApplication() throws Exception {
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);
//        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(restSuccess(assessment));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competitionResource.getId(), application.getId()))
                .andExpect(view().name(applicationReview))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetition())))
                .andExpect(model().attribute("assessment", assessment));
        /** TODO: also test attribute partners {@link AssessmentController#153} */

    }

    @Ignore
    @Test
    public void testApplicationAssessmentDetailsRejectedApplication() throws Exception {
        ApplicationResource application = applications.get(0);
        Assessment assessment = getAssessment(application);
//        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(restSuccess(assessment));

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competitionResource.getId(), application.getId()))
                .andExpect(view().name(assessorDashboard))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetition())))
                .andExpect(model().attribute("assessment", assessment));

    }

    @Ignore
    @Test
    public void testApplicationAssessmentDetailsInvalidApplication() throws Exception {
        ApplicationResource application = applications.get(2);
        Assessment assessment = getAssessment(application);
        //when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(restSuccess(assessment));

        when(applicationService.getById(anyLong())).thenReturn(application);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getById(anyLong())).thenReturn(null);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competitionResource.getId(), application.getId()))
                .andExpect(view().name(assessmentDetails))
                .andExpect(model().attribute("userOrganisation", application3Organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(application3Organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItems(application3Organisations.get(0))))
                .andExpect(model().attribute("leadOrganisation", application3Organisations.get(0)))
                .andExpect(model().attribute("currentApplication", application))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(application.getCompetition())));
    }


    @Test
    public void testUpdateQuestionAssessmentFeedbackValid() throws Exception {
        ApplicationResource application = applications.get(1);


        when(responseService.saveQuestionResponseAssessorFeedback(assessor.getId(), 26L,
                Optional.of("Some Feedback Value"), Optional.of("Some Feedback Text")))
                .thenReturn(restSuccess(OK));

        mockMvc.perform(
                put("/assessor/competitions/{competitionId}/applications/{applicationId}/response/{responseId}"
                        , competitionResource.getId(), application.getId(), "26")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("feedbackValue", "Some Feedback Value")
                        .param("feedbackText", "Some Feedback Text")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateQuestionAssessmentFeedbackInvalid() throws Exception {
        ApplicationResource application = applications.get(1);

        when(responseService.saveQuestionResponseAssessorFeedback(assessor.getId(), 26L,
                Optional.of("Some Feedback Value"), Optional.of("Some Feedback Text")))
                .thenReturn(restFailure(BAD_REQUEST));

        mockMvc.perform(
                put("/assessor/competitions/{competitionId}/applications/{applicationId}/response/{responseId}"
                        , competitionResource.getId(), application.getId(), "26")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("feedbackValue", "Some Feedback Value")
                        .param("feedbackText", "Some Feedback Text")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Ignore
    @Test
    public void testApplicationAssessmentDetailsReject() throws Exception {
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);
//        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(restSuccess(assessment));

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}/reject-invitation", competitionResource.getId(), application.getId()))
                .andExpect(view().name(rejectInvitation))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetition())))
                .andExpect(model().attribute("assessment", assessment));
    }

    @Ignore
    @Test
    public void testGetAssessmentSubmitReview() throws Exception {
        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}/summary", competitionResource.getId(), 1L))
                .andExpect(view().name(assessmentSubmitReview))
                .andExpect(model().attributeExists("model"));
    }

    @Ignore
    @Test
    public void testInvitationAnswerReject() throws Exception {
        ProcessRoleResource assessorProcessRole = assessorProcessRoleResources.get(0);

        String reason = "Decline because of 123";
        String observations = "Observations 12345678";
        mockMvc.perform(
                post("/assessor/invitation_answer")
                        .param("reject", "a")
                        .param("competitionId", "1")
                        .param("applicationId", String.valueOf(assessorProcessRole.getApplication()))
                        .param("decisionReason", reason)
                        .param("observations", observations)
        ).andExpect(status().is3xxRedirection());
        Mockito.inOrder(assessmentRestService)
                .verify(assessmentRestService, calls(1))
                .rejectAssessmentInvitation(eq(assessorProcessRole.getId()), any(ProcessOutcome.class));
    }

    @Ignore
    @Test
    public void testInvitationAnswerAccept() throws Exception {
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(
                post("/assessor/invitation_answer")
                        .param("accept", "a")
                        .param("competitionId", "1")
                        .param("applicationId", String.valueOf(application.getId()))
        ).andExpect(status().is3xxRedirection());
//        Mockito.inOrder(assessmentRestService)
//                .verify(assessmentRestService, calls(1))
//                .acceptAssessmentInvitation(eq(assessment.getProcessRole().getId()), any(Assessment.class));
    }

    @Ignore
    @Test
    public void testAssessmentsSubmissions() throws Exception {
        Set<Long> assessmentSet = new HashSet<>();
        assessmentSet.add(assessments.get(0).getId());
        assessmentSet.add(assessments.get(1).getId());

        mockMvc.perform(
                post("/assessor/submit-assessments")
                        .param("submit_assessments", "")
                        .param("submitted[]", assessments.get(0).getId().toString(), assessments.get(1).getId().toString())
                        .param("competitionId", String.valueOf(competitionResource.getId()))
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/assessor/competitions/" + competitionResource.getId() + "/applications"));

        Mockito.inOrder(assessmentRestService).verify(assessmentRestService, calls(1)).submitAssessments(assessor.getId(), assessmentSet);
    }

    @Test
    public void testAssessmentSummaryComplete() throws Exception {
        ApplicationResource application = applications.get(1);

        String feedback = "just because 345678";
        String isSuitable = "Yes, suitable for funding";
        String comments = "comment; x";
        mockMvc.perform(
                post("/assessor/competitions/{competitionId}/applications/{applicationId}/complete",
                        application.getCompetition(),
                        application.getId()
                )
                        .param("confirm-submission", "")
                        .param("is-suitable-for-funding", isSuitable)
                        .param("suitable-for-funding-feedback", feedback)
                        .param("comments-to-share", comments)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/assessor/competitions/" + competitionResource.getId() + "/applications"));

        Mockito.inOrder(assessmentRestService).verify(assessmentRestService, calls(1)).saveAssessmentSummary(assessor.getId(), application.getId(), isSuitable, feedback, comments);
    }

    private Assessment getAssessment(ApplicationResource application) {
//        Optional<Assessment> optionalAssessment = assessments.stream().filter(a -> a.getProcessRole().getApplication().getId().equals(application.getId())).findFirst();
//        assertTrue(optionalAssessment.isPresent());
//        return optionalAssessment.get();
        return null;
    }
}
