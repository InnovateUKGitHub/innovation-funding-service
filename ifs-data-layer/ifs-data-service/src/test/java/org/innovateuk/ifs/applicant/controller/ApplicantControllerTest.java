package org.innovateuk.ifs.applicant.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.dashboard.*;
import org.innovateuk.ifs.applicant.transactional.ApplicantService;
import org.innovateuk.ifs.application.transactional.ApplicationDashboardService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResponseResourceBuilder.newApplicantFormInputResponseResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource.ApplicantDashboardResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardEuGrantTransferRowResource.DashboardApplicationForEuGrantTransferResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardInProgressRowResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardInSetupRowResource.DashboardInSetupRowResourceBuilder.aDashboardInSetupRowResource;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicantControllerTest extends BaseControllerMockMVCTest<ApplicantController> {

    private static final long USER_ID = 1L;
    private static final long QUESTION_ID = 1L;
    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 1L;

    @Mock
    private ApplicantService applicationService;

    @Mock
    private ApplicationDashboardService applicationDashboardService;

    @Override
    protected ApplicantController supplyControllerUnderTest() {
        return new ApplicantController();
    }

    @Test
    public void getQuestion() throws Exception {

        when(applicationService.getQuestion(USER_ID, QUESTION_ID, APPLICATION_ID)).thenReturn(serviceSuccess(applicantQuestionResource().build()));

        mockMvc.perform(get("/applicant/{user}/{application}/question/{question}", USER_ID, APPLICATION_ID, QUESTION_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getSection() throws Exception {

        when(applicationService.getSection(USER_ID, SECTION_ID, APPLICATION_ID)).thenReturn(serviceSuccess(applicantSectionResource().build()));

        mockMvc.perform(get("/applicant/{user}/{application}/section/{section}", USER_ID, APPLICATION_ID, QUESTION_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

    }

    @Test
    public void  getApplicantDashboard() throws Exception {
        long euGrantTransferId = 1L;
        long inProgressId = 2L;
        long inSetupId = 3L;
        long previousId = 4L;

        DashboardEuGrantTransferRowResource euGrantTransfer = new DashboardApplicationForEuGrantTransferResourceBuilder().withApplicationId(euGrantTransferId).build();
        DashboardInProgressRowResource inProgress = new DashboardApplicationInProgressResourceBuilder().withApplicationId(inProgressId).build();
        DashboardInSetupRowResource inSetup = aDashboardInSetupRowResource().withApplicationId(inSetupId).build();
        DashboardPreviousRowResource previous = new DashboardPreviousApplicationResourceBuilder().withApplicationId(previousId).build();

        when(applicationDashboardService.getApplicantDashboard(USER_ID)).thenReturn(serviceSuccess(new ApplicantDashboardResourceBuilder()
                .withEuGrantTransfer(singletonList(euGrantTransfer))
                .withInProgress(singletonList(inProgress))
                .withInSetup(singletonList(inSetup))
                .withPrevious(singletonList(previous))
                .build()));

        MvcResult mvcResult = mockMvc.perform(get("/applicant/{user}/applications/dashboard", USER_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andReturn();

        ApplicantDashboardResource result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApplicantDashboardResource.class);

        assertEquals(euGrantTransferId, result.getEuGrantTransfer().get(0).getApplicationId());
        assertEquals(inProgressId, result.getInProgress().get(0).getApplicationId());
        assertEquals(inSetupId, result.getInSetup().get(0).getApplicationId());
        assertEquals(previousId, result.getPrevious().get(0).getApplicationId());
    }

    private ApplicantSectionResourceBuilder applicantSectionResource() {
        return newApplicantSectionResource()
                .withApplication(newApplicationResource().build())
                .withCompetition(newCompetitionResource().build())
                .withCurrentApplicant(applicantResource().build())
                .withCurrentUser(newUserResource().build())
                .withApplicants(applicantResource().build(3))

                .withSection(newSectionResource().build())
                .withApplicantParentSection(newApplicantSectionResource().build())
                .withApplicantChildrenSections(newApplicantSectionResource().build(3))
                .withApplicantQuestions(applicantQuestionResource().build(3));
    }

    private ApplicantQuestionResourceBuilder applicantQuestionResource() {
        return newApplicantQuestionResource()
                .withApplication(newApplicationResource().build())
                .withCompetition(newCompetitionResource().build())
                .withCurrentApplicant(applicantResource().build())
                .withCurrentUser(newUserResource().build())
                .withApplicants(applicantResource().build(3))

                .withQuestion(newQuestionResource().build())
                .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                        .withStatus(newQuestionStatusResource().build())
                        .withAssignedBy(applicantResource().build())
                        .withAssignee(applicantResource().build())
                        .withMarkedAsCompleteBy(applicantResource().build())
                        .build(3))
                .withApplicantFormInputs(newApplicantFormInputResource()
                        .withFormInput(newFormInputResource().build())
                        .withApplicantResponses(newApplicantFormInputResponseResource()
                            .withApplicant(applicantResource().build())
                            .withResponse(newFormInputResponseResource().build())
                            .build(3))
                        .build(3));
    }

    private ApplicantResourceBuilder applicantResource() {
        return newApplicantResource()
                .withProcessRole(newProcessRoleResource().build())
                .withOrganisation(newOrganisationResource().build());
    }

    private static final FieldDescriptor[] questionFields = {
            fieldWithPath("application").description("The resource representing an application."),
            fieldWithPath("competition").description("The resource representing a competition."),
            fieldWithPath("currentApplicant").description("The resource representing the current applicant and their role and organisation on the application."),
            fieldWithPath("currentApplicant.processRole").description("The resource representing the applicants role on the application"),
            fieldWithPath("currentApplicant.organisation").description("The resource representing the applicants organisation in the application."),
            fieldWithPath("currentUser").description("The resource representing the current user."),
            fieldWithPath("applicants[]").description("The resources representing all applicants and their roles and organisations on the application."),

            fieldWithPath("question").description("The resource representing an application question."),
            fieldWithPath("applicantFormInputs[]").description("The resources representing a form input and its responses for the application."),
            fieldWithPath("applicantFormInputs[].formInput").description("The resource representing a form input."),
            fieldWithPath("applicantFormInputs[].applicantResponses[]").description("The resources representing a responses to a form input and who responded for the application."),
            fieldWithPath("applicantFormInputs[].applicantResponses[].response").description("The resource representing a responses to a form input and who responded for the application."),
            fieldWithPath("applicantFormInputs[].applicantResponses[].applicant").description("The resource representing an applicant that responded to the form input."),
            fieldWithPath("applicantQuestionStatuses[]").description("The resources representing an the status of a question and who is assigned to or completed by on the application."),
            fieldWithPath("applicantQuestionStatuses[].status").description("The resource representing the status of a question."),
            fieldWithPath("applicantQuestionStatuses[].markedAsCompleteBy").description("The resource representing the applicant who marked the question as complete."),
            fieldWithPath("applicantQuestionStatuses[].assignee").description("The resource representing the applicant who is assigned to the question."),
            fieldWithPath("applicantQuestionStatuses[].assignedBy").description("The resource representing the applicant who assigned the question.")
    };

    private static final FieldDescriptor[] sectionFields = {
            fieldWithPath("application").description("The resource representing an application."),
            fieldWithPath("competition").description("The resource representing a competition."),
            fieldWithPath("currentApplicant").description("The resource representing the current applicant and their role and organisation on the application."),
            fieldWithPath("currentApplicant.processRole").description("The resource representing the applicants role on the application"),
            fieldWithPath("currentApplicant.organisation").description("The resource representing the applicants organisation in the application."),
            fieldWithPath("currentUser").description("The resource representing the current user."),
            fieldWithPath("applicants[]").description("The resources representing all applicants and their roles and organisations on the application."),

            fieldWithPath("section").description("The resource representing an application section."),
            fieldWithPath("applicantParentSection").description("The resource representing the parent applicant section."),
            fieldWithPath("applicantChildrenSections[]").description("The resource representing the children applicant sections."),
            fieldWithPath("applicantQuestions[]").description("The resource representing the applicant question for this section.")
    };

    private static final FieldDescriptor[] sectionFieldsWithoutCurrentApplicant = {
            fieldWithPath("application").description("The resource representing an application."),
            fieldWithPath("competition").description("The resource representing a competition."),
            fieldWithPath("currentApplicant").description("The resource representing the current applicant and their role and organisation on the application."),
            fieldWithPath("currentUser").description("The resource representing the current user."),
            fieldWithPath("applicants[]").description("The resources representing all applicants and their roles and organisations on the application."),

            fieldWithPath("section").description("The resource representing an application section."),
            fieldWithPath("applicantParentSection").description("The resource representing the parent applicant section."),
            fieldWithPath("applicantChildrenSections[]").description("The resource representing the children applicant sections."),
            fieldWithPath("applicantQuestions[]").description("The resource representing the applicant question for this section.")
    };
}