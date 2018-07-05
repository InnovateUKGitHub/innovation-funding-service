package org.innovateuk.ifs.applicant.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.controller.ApplicantController;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.transactional.ApplicantService;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.documentation.*;
import org.innovateuk.ifs.form.documentation.FormInputDocumentationTest;
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
                        .andWithPrefix("application.", ApplicationDocs.applicationResourceFields)
                        .andWithPrefix("competition.", CompetitionResourceDocs.competitionResourceFields)
                        .andWithPrefix("currentApplicant.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("currentApplicant.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("currentApplicant.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("currentUser.", UserDocs.userResourceFields)
                        .andWithPrefix("applicants[].", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicants[].processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicants[].organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("question.", QuestionDocs.questionFields)
                        .andWithPrefix("applicantFormInputs[].", ApplicationFormInputDocs.applicationFormResourceFields)
                        .andWithPrefix("applicantFormInputs[].formInput.", FormInputDocs.formInputFields)
                        .andWithPrefix("applicantFormInputs[].applicantResponses[].", ApplicantFormInputResponseResourceDocs.applicantFormInputResponseResourceFields)
                        .andWithPrefix("applicantFormInputs[].applicantResponses[].applicant.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantFormInputs[].applicantResponses[].applicant.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantFormInputs[].applicantResponses[].applicant.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantFormInputs[].applicantResponses[].response.", FormInputResponseResourceDocs.formInputResponseResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].", ApplicantQuestionStatusResourceDocs.applicantQuestionStatusResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].status.", QuestionStatusResourceDocs.questionStatusResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].markedAsCompleteBy.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].markedAsCompleteBy.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].markedAsCompleteBy.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].assignee.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].assignee.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].assignee.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].assignedBy.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].assignedBy.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestionStatuses[].assignedBy.organisation.", OrganisationDocs.organisationResourceFields)
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
                        .andWithPrefix("application.", ApplicationDocs.applicationResourceFields)
                        .andWithPrefix("competition.", CompetitionResourceDocs.competitionResourceFields)
                        .andWithPrefix("currentApplicant.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("currentApplicant.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("currentApplicant.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("currentUser.", UserDocs.userResourceFields)
                        .andWithPrefix("applicants[].", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicants[].processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicants[].organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("section.", SectionDocs.sectionResourceFields)
                        .andWithPrefix("applicantQuestions[].application.", ApplicationDocs.applicationResourceFields)
                        .andWithPrefix("applicantQuestions[].competition.", CompetitionResourceDocs.competitionResourceFields)
                        .andWithPrefix("applicantQuestions[].currentApplicant.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestions[].currentApplicant.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestions[].currentApplicant.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestions[].currentUser.", UserDocs.userResourceFields)
                        .andWithPrefix("applicantQuestions[].applicants[].", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestions[].applicants[].processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestions[].applicants[].organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestions[].question.", QuestionDocs.questionFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].", ApplicationFormInputDocs.applicationFormResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].formInput.", FormInputDocs.formInputFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].applicantResponses[].", ApplicantFormInputResponseResourceDocs.applicantFormInputResponseResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].applicantResponses[].applicant.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].applicantResponses[].applicant.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].applicantResponses[].applicant.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantFormInputs[].applicantResponses[].response.", FormInputResponseResourceDocs.formInputResponseResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].", ApplicantQuestionStatusResourceDocs.applicantQuestionStatusResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].status.", QuestionStatusResourceDocs.questionStatusResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].markedAsCompleteBy.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].markedAsCompleteBy.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].markedAsCompleteBy.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].assignee.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].assignee.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].assignee.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].assignedBy.", ApplicantDocs.applicantResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].assignedBy.processRole.", ProcessRoleDocs.processRoleResourceFields)
                        .andWithPrefix("applicantQuestions[].applicantQuestionStatuses[].assignedBy.organisation.", OrganisationDocs.organisationResourceFields)
                        .andWithPrefix("applicantParentSection.", sectionFieldsWithoutCurrentApplicant)
                        .andWithPrefix("applicantChildrenSections[].", sectionFieldsWithoutCurrentApplicant)
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