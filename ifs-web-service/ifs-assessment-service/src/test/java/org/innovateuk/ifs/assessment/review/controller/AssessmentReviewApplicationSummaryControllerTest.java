package org.innovateuk.ifs.assessment.review.controller;

import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.HashSet;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentReviewApplicationSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentReviewApplicationSummaryController> {

    @Spy
    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected AssessmentReviewApplicationSummaryController supplyControllerUnderTest() {
        return new AssessmentReviewApplicationSummaryController();
    }

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();

        questionResources.forEach((id, questionResource) -> when(applicantRestService.getQuestion(any(), any(), eq(questionResource.getId()))).thenReturn(newApplicantQuestionResource().build()));
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void testApplicationSummary() throws Exception {
        long reviewId = 1L;
        ApplicationResource app = applications.get(0);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));

        mockMvc.perform(get("/review/{reviewId}/application/{applicationId}", reviewId, app.getId()   ))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-application-overview"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(application1Organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(1))))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)));
    }
}
