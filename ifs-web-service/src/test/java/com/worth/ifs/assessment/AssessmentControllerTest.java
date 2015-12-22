package com.worth.ifs.assessment;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.viewmodel.AssessmentDashboardModel;
import com.worth.ifs.user.domain.ProcessRole;
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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentControllerTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentController assessmentController;

    /* pages */
    private final String competitionAssessments = "assessor-competition-applications";
    private final String assessorDashboard = "assessor-dashboard";
    private final String assessmentDetails = "assessment-details";
    private final String assessmentSubmitReview = "assessment-submit-review";
    private final String applicationReview = "application-assessment-review";
    private final String rejectInvitation = "reject-assessment-invitation";


    @Before
    public void setUp() {
        super.setup();

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(assessmentController)
                .setViewResolvers(viewResolver())
                .build();

        this.setupCompetition();
        this.setupUserRoles();
        this.loginUser(assessor);
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupAssessment();
    }

    @Test
    public void testCompetitionAssessmentDashboard() throws Exception {
        List<Assessment> nonSubmittedAssessments = assessments.stream().filter(a -> !a.isSubmitted()).collect(toList());
        nonSubmittedAssessments.sort(new AssessmentStatusComparator());

        MvcResult mvcResult = mockMvc.perform(get("/assessor/competitions/{competitionId}/applications", competition.getId())).andReturn();
        AssessmentDashboardModel model = this.attributeFromMvcResultModel(mvcResult, "model");
        assertNotNull(model);
        assertEquals(2, model.getAssessments().size());
        assertEquals(nonSubmittedAssessments.get(0).getId(), model.getAssessments().get(0).getAssessment().getId());
        assertEquals(submittedAssessments.get(0).getId(), model.getSubmittedAssessments().get(0).getAssessment().getId());
        assertEquals(competition.getId(), model.getCompetition().getId());
    }

    @Test
    public void testUserIsNotAssessorOnApplication() throws Exception {

        this.loginUser(applicant);
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competition.getId(), application.getId()))
                .andExpect(view().name(assessorDashboard))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetitionId())))
                .andExpect(model().attributeDoesNotExist("assessment"));
    }

    @Test
    public void testApplicationAssessmentDetailsPendingApplication() throws Exception {
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);
        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(assessment);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competition.getId(), application.getId()))
                .andExpect(view().name(applicationReview))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetitionId())))
                .andExpect(model().attribute("assessment", assessment));
        /** TODO: also test attribute partners {@link AssessmentController#153} */

    }

    @Test
    public void testApplicationAssessmentDetailsRejectedApplication() throws Exception {
        ApplicationResource application = applications.get(0);
        Assessment assessment = getAssessment(application);
        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(assessment);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competition.getId(), application.getId()))
                .andExpect(view().name(assessorDashboard))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetitionId())))
                .andExpect(model().attribute("assessment", assessment));

    }

    @Test
    public void testApplicationAssessmentDetailsInvalidApplication() throws Exception {
        ApplicationResource application = applications.get(2);
        Assessment assessment = getAssessment(application);
        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(assessment);

        when(applicationService.getById(anyLong())).thenReturn(application);

        log.info("assessment status: " + assessment.getProcessStatus());
        log.info("Application we use for assessment test: " + application.getId());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}", competition.getId(), application.getId()))
                .andExpect(view().name(assessmentDetails))
                .andExpect(model().attribute("userOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItems(organisations.get(0), organisations.get(1))))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("currentApplication", application))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(application.getCompetitionId())));
    }


    @Test
    public void testUpdateQuestionAssessmentFeedbackValid() throws Exception {
        ApplicationResource application = applications.get(1);


        when(responseService.saveQuestionResponseAssessorFeedback(assessor.getId(), 26L,
                Optional.of("Some Feedback Value"), Optional.of("Some Feedback Text")))
                .thenReturn(true);

        mockMvc.perform(
                put("/assessor/competitions/{competitionId}/applications/{applicationId}/response/{responseId}"
                        , competition.getId(), application.getId(), "26")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("feedbackValue", "Some Feedback Value")
                        .param("feedbackText", "Some Feedback Text")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateQuestionAssessmentFeedbackInvalid() throws Exception {
        ApplicationResource application = applications.get(1);

        when(responseService.saveQuestionResponseAssessorFeedback(assessor.getId(), 26L,
                Optional.of("Some Feedback Value"), Optional.of("Some Feedback Text")))
                .thenReturn(false);

        mockMvc.perform(
                put("/assessor/competitions/{competitionId}/applications/{applicationId}/response/{responseId}"
                        , competition.getId(), application.getId(), "26")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("feedbackValue", "Some Feedback Value")
                        .param("feedbackText", "Some Feedback Text")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testApplicationAssessmentDetailsReject() throws Exception {
        ApplicationResource application = applications.get(1);
        Assessment assessment = getAssessment(application);
        when(assessmentRestService.getOneByProcessRole(assessment.getProcessRole().getId())).thenReturn(assessment);

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}/reject-invitation", competition.getId(), application.getId()))
                .andExpect(view().name(rejectInvitation))
                .andExpect(model().attribute("competition", competitionService.getById(application.getCompetitionId())))
                .andExpect(model().attribute("assessment", assessment));
    }

    @Ignore
    @Test
    public void testGetAssessmentSubmitReview() throws Exception {
        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications/{applicationId}/summary", competition.getId(), 1L))
                .andExpect(view().name(assessmentSubmitReview))
                .andExpect(model().attributeExists("model"));
    }

    @Test
    public void testInvitationAnswerReject() throws Exception {
        ProcessRole assessorProcessRole = assessorProcessRoles.get(0);

        String reason = "Decline because of 123";
        String observations = "Observations 12345678";
        mockMvc.perform(
                post("/assessor/invitation_answer")
                        .param("reject", "a")
                        .param("competitionId", "1")
                        .param("applicationId", String.valueOf(assessorProcessRole.getApplication().getId()))
                        .param("decisionReason", reason)
                        .param("observations", observations)
        ).andExpect(status().is3xxRedirection());
        Mockito.inOrder(assessmentRestService)
                .verify(assessmentRestService, calls(1))
                .rejectAssessmentInvitation(eq(assessorProcessRole.getId()), any(ProcessOutcome.class));
    }

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
        Mockito.inOrder(assessmentRestService)
                .verify(assessmentRestService, calls(1))
                .acceptAssessmentInvitation(eq(assessment.getProcessRole().getId()), any(Assessment.class));
    }

    @Test
    public void testAssessmentsSubmissions() throws Exception {
        Set<Long> assessmentSet = new HashSet<>();
        assessmentSet.add(assessments.get(0).getId());
        assessmentSet.add(assessments.get(1).getId());

        mockMvc.perform(
                post("/assessor/submit-assessments")
                        .param("submit_assessments", "")
                        .param("submitted[]", assessments.get(0).getId().toString(), assessments.get(1).getId().toString())
                        .param("competitionId", String.valueOf(competition.getId()))
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/assessor/competitions/" + competition.getId() + "/applications"));

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
                        application.getCompetitionId(),
                        application.getId()
                )
                        .param("confirm-submission", "")
                        .param("is-suitable-for-funding", isSuitable)
                        .param("suitable-for-funding-feedback", feedback)
                        .param("comments-to-share", comments)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/assessor/competitions/" + competition.getId() + "/applications"));

        Mockito.inOrder(assessmentRestService).verify(assessmentRestService, calls(1)).saveAssessmentSummary(assessor.getId(), application.getId(), isSuitable, feedback, comments);
    }

    private Assessment getAssessment(ApplicationResource application) {
        Optional<Assessment> optionalAssessment = assessments.stream().filter(a -> new ApplicationResource(a.getProcessRole().getApplication()).equals(application)).findFirst();
        assertTrue(optionalAssessment.isPresent());
        return optionalAssessment.get();
    }
}
