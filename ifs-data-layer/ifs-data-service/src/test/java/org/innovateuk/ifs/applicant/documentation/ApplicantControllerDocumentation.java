package org.innovateuk.ifs.applicant.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.controller.ApplicantController;
import org.innovateuk.ifs.applicant.transactional.ApplicantService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResponseResourceBuilder.newApplicantFormInputResponseResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicantControllerDocumentation extends BaseControllerMockMVCTest<ApplicantController> {

    private static final long USER_ID = 1L;
    private static final long QUESTION_ID = 1L;
    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 1L;

    @Mock
    private ApplicantService applicationService;

    @Override
    protected ApplicantController supplyControllerUnderTest() {
        return new ApplicantController();
    }

    @Test
    public void getQuestion() throws Exception {

        when(applicationService.getQuestion(USER_ID, QUESTION_ID, APPLICATION_ID)).thenReturn(serviceSuccess(applicantQuestionResource().build()));

        mockMvc.perform(get("/applicant/{user}/{application}/question/{question}", USER_ID, APPLICATION_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andDo(document("applicant/{method-name}",
                        pathParameters(
                                parameterWithName("user").description("Id of the user"),
                                parameterWithName("application").description("Id of the application"),
                                parameterWithName("question").description("Id of the question")
                        ),
                        responseFields(questionFields)
                ));

    }

    @Test
    public void getSection() throws Exception {

        when(applicationService.getSection(USER_ID, SECTION_ID, APPLICATION_ID)).thenReturn(serviceSuccess(applicantSectionResource().build()));

        mockMvc.perform(get("/applicant/{user}/{application}/section/{section}", USER_ID, APPLICATION_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andDo(document("applicant/{method-name}",
                        pathParameters(
                                parameterWithName("user").description("Id of the user"),
                                parameterWithName("application").description("Id of the application"),
                                parameterWithName("section").description("Id of the section")
                        ),
                        responseFields(sectionFields)
                ));

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
}