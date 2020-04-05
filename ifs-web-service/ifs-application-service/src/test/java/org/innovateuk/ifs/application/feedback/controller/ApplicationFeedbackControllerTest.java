package org.innovateuk.ifs.application.feedback.controller;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.feedback.populator.ApplicationFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.feedback.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentAggregateResourceBuilder.newApplicationAssessmentAggregateResource;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ApplicationFeedbackControllerTest extends AbstractApplicationMockMVCTest<ApplicationFeedbackController> {

    @Spy
    @InjectMocks
    private ApplicationFeedbackViewModelPopulator applicationFeedbackViewModelPopulator;

    @Spy
    @InjectMocks
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;

    @Mock
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Mock
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private InterviewResponseRestService interviewResponseRestService;

    @Mock
    private ProjectService projectService;

    @Before
    public void setUpData() {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();

        questionResources.forEach((id, questionResource) -> when(applicantRestService.getQuestion(any(), any(), eq(questionResource.getId()))).thenReturn(newApplicantQuestionResource().build()));
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void upload() throws Exception {
        ApplicationAssessmentAggregateResource aggregateResource = newApplicationAssessmentAggregateResource()
                .withScopeAssessed(true)
                .withTotalScope(5)
                .withInScope(4)
                .withScores(ImmutableMap.of(1L, new BigDecimal("2")))
                .withAveragePercentage(BigDecimal.valueOf(3))
                .build();

        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        ApplicationResource app = applications.get(0);
        app.setApplicationState(ApplicationState.APPROVED);
        app.setCompetitionStatus(ASSESSOR_FEEDBACK);
        app.setCompetition(competitionResource.getId());
        setupMocksForGet(app, aggregateResource, expectedFeedback);

        when(interviewResponseRestService.uploadResponse(app.getId(), "application/pdf", 11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restSuccess());

        MockMultipartFile file = new MockMultipartFile("response", "testFile.pdf", "application/pdf", "My content!".getBytes());

        mockMvc.perform(
                multipart("/application/" + app.getId() + "/feedback")
                        .file(file)
                        .param("uploadResponse", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-feedback"));

        verify(interviewResponseRestService).uploadResponse(app.getId(), "application/pdf", 11, "testFile.pdf", "My content!".getBytes());
    }

    @Test
    public void remove() throws Exception {
        ApplicationAssessmentAggregateResource aggregateResource = newApplicationAssessmentAggregateResource()
                .withScopeAssessed(true)
                .withTotalScope(5)
                .withInScope(4)
                .withScores(ImmutableMap.of(1L, new BigDecimal("2")))
                .withAveragePercentage(BigDecimal.valueOf(3))
                .build();

        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        ApplicationResource app = applications.get(0);
        app.setApplicationState(ApplicationState.APPROVED);
        app.setCompetitionStatus(ASSESSOR_FEEDBACK);
        app.setCompetition(competitionResource.getId());
        setupMocksForGet(app, aggregateResource, expectedFeedback);

        UserResource userResource = newUserResource().build();
        ProcessRoleResource processRole = newProcessRoleResource()
                .withOrganisation(organisations.get(0).getId()).build();

        when(userRestService.findProcessRole(userResource.getId(), app.getId())).thenReturn(restSuccess(processRole));
        when(organisationRestService.getOrganisationById(processRole.getOrganisationId())).thenReturn(restSuccess(organisations.get(0)));

        when(interviewResponseRestService.deleteResponse(app.getId()))
                .thenReturn(restSuccess());

        mockMvc.perform(
                post("/application/" + app.getId() + "/feedback")
                        .param("removeResponse", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-feedback"));

        verify(interviewResponseRestService).deleteResponse(app.getId());
    }


    private void setupMocksForGet(ApplicationResource app, ApplicationAssessmentAggregateResource aggregateResource,
                                  ApplicationAssessmentFeedbackResource expectedFeedback) {
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).withRole(Role.LEADAPPLICANT).build();
        when(userRestService.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));
        when(organisationService.getLeadOrganisation(anyLong(),any())).thenReturn(new OrganisationResource());

        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId()))
                .thenReturn(restSuccess(aggregateResource));

        when(assessmentRestService.getApplicationFeedback(app.getId())).thenReturn(restSuccess(expectedFeedback));

        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(true));

        when(interviewResponseRestService.findResponse(app.getId())).thenReturn(restSuccess(newFileEntryResource().withName("Name").build()));
        when(interviewAssignmentRestService.findFeedback(app.getId())).thenReturn(restSuccess(newFileEntryResource().withName("Name").build()));
    }

    @Override
    protected ApplicationFeedbackController supplyControllerUnderTest() {
        return new ApplicationFeedbackController();
    }
}
