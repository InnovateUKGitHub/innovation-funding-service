package com.worth.ifs.assessment;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import org.hamcrest.Matchers;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
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
        List<Assessment> nonSubmittedAssessments = assessments.stream().filter(assessment -> !assessment.isSubmitted()).collect(Collectors.toList());

        mockMvc.perform(get("/assessor/competitions/{competitionId}/applications", competition.getId()))
                .andExpect(view().name(competitionAssessments))
                .andExpect(model().attribute("competition", competition))
                .andExpect(model().attribute("assessments", Matchers.hasSize(nonSubmittedAssessments.size())))
                .andExpect(model().attribute("assessments", Matchers.hasItems(nonSubmittedAssessments.get(0), nonSubmittedAssessments.get(1))))
                .andExpect(model().attribute("submittedAssessments", submittedAssessments));

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
        ApplicationResource application = applications.get(1);

        String reason = "Decline because of 123";
        String observations = "Observations 12345678";
        mockMvc.perform(
                post("/assessor/invitation_answer")
                        .param("reject", "a")
                        .param("competitionId", "1")
                        .param("applicationId", String.valueOf(application.getId()))
                        .param("decisionReason", reason)
                        .param("observations", observations)
        ).andExpect(status().is3xxRedirection());
        Mockito.inOrder(assessmentRestService).verify(assessmentRestService, calls(1)).rejectAssessmentInvitation(eq(application.getId()), eq(assessor.getId()), any(Assessment.class));
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
        Mockito.inOrder(assessmentRestService).verify(assessmentRestService, calls(1)).acceptAssessmentInvitation(eq(application.getId()), eq(assessor.getId()), any(Assessment.class));
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
        Double overallScore = 60.0;
        mockMvc.perform(
                post("/assessor/competitions/{competitionId}/applications/{applicationId}/complete",
                        application.getCompetitionId(),
                        application.getId()
                )
                        .param("confirm-submission", "")
                        .param("is-suitable-for-funding", isSuitable)
                        .param("suitable-for-funding-feedback", feedback)
                        .param("comments-to-share", comments)
                        .param("overall-score", overallScore + "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/assessor/competitions/" + competition.getId() + "/applications"));

        Mockito.inOrder(assessmentRestService).verify(assessmentRestService, calls(1)).saveAssessmentSummary(assessor.getId(), application.getId(), isSuitable, feedback, comments, overallScore);
    }

    private Assessment getAssessment(ApplicationResource application) {
        Optional<Assessment> optionalAssessment = assessments.stream().filter(a -> new ApplicationResource(a.getApplication()).equals(application)).findFirst();
        Assert.assertTrue(optionalAssessment.isPresent());
        return optionalAssessment.get();
    }
}