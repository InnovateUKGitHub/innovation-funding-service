package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.application.transactional.FormInputResponseServiceImpl;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.ArgumentMatchers.isA;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in {@link FormInputService} interact with Spring Security
 */
public class FormInputServiceSecurityTest extends BaseServiceSecurityTest<FormInputResponseService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    private FormInputResponsePermissionRules formInputResponsePermissionRules;

    @Before
    public void lookupPermissionRules() {
        formInputResponsePermissionRules = getMockPermissionRulesBean(FormInputResponsePermissionRules.class);
    }

    @Test
    public void testFindResponsesByApplication() {
        long applicationId = 1L;

        when(classUnderTestMock.findResponsesByApplication(applicationId))
                .thenReturn(serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.findResponsesByApplication(applicationId);

        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA
                        (FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .assessorCanSeeTheInputResponsesInApplicationsTheyAssess(isA(FormInputResponseResource.class), isA
                        (UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .internalUserCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA
                        (UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource
                        .class), isA(UserResource.class));
    }

    @Test
    public void testFindResponsesByFormInputIdAndApplicationId() {
        long applicationId = 1L;
        long formInputResponseId = 2L;

        when(classUnderTestMock.findResponsesByFormInputIdAndApplicationId(applicationId, formInputResponseId))
                .thenReturn(serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.findResponsesByFormInputIdAndApplicationId(applicationId, formInputResponseId);

        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA
                        (FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .assessorCanSeeTheInputResponsesInApplicationsTheyAssess(isA(FormInputResponseResource.class), isA
                        (UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .internalUserCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA
                        (UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource
                        .class), isA(UserResource.class));
    }


    @Test
    public void testSaveQuestionResponse() {
        final long applicationId = 1L;
        final long formInputId = 2L;
        final long userId = 3L;

        final FormInputResponseCommand formInputResponseCommand = new FormInputResponseCommand(formInputId,
                applicationId, userId, "test text");

        assertAccessDenied(
                () -> classUnderTest.saveQuestionResponse(formInputResponseCommand),
                () -> verify(formInputResponsePermissionRules)
                        .aConsortiumMemberCanUpdateAFormInputResponse(isA(FormInputResponseCommand.class), isA
                                (UserResource.class))
        );
    }

    @Test
    public void findResponseByApplicationIdAndQuestionSetupType() {
        when(classUnderTestMock.findResponseByApplicationIdAndQuestionSetupType(1L, PROJECT_SUMMARY))
                .thenReturn(serviceSuccess(newFormInputResponseResource().build()));

        assertAccessDenied(
                () -> classUnderTest.findResponseByApplicationIdAndQuestionSetupType(1L, PROJECT_SUMMARY),
                () -> {
                    verify(formInputResponsePermissionRules)
                            .consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(
                                    isA(FormInputResponseResource.class), isA(UserResource.class));
                    verify(formInputResponsePermissionRules)
                            .assessorCanSeeTheInputResponsesInApplicationsTheyAssess(
                                    isA(FormInputResponseResource.class), isA(UserResource.class));
                    verify(formInputResponsePermissionRules)
                            .internalUserCanSeeFormInputResponsesForApplications(
                                    isA(FormInputResponseResource.class), isA(UserResource.class));
                    verify(formInputResponsePermissionRules)
                            .consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(
                                    isA(FormInputResponseResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void findResponseByApplicationIdAndQuestionId() {
        when(classUnderTestMock.findResponseByApplicationIdAndQuestionId(1L, 2L))
                .thenReturn(serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.findResponseByApplicationIdAndQuestionId(1L, 2L);

        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA
                        (FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .assessorCanSeeTheInputResponsesInApplicationsTheyAssess(isA(FormInputResponseResource.class), isA
                        (UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .internalUserCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA
                        (UserResource.class));
        verify(formInputResponsePermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource
                        .class), isA(UserResource.class));
    }

    @Override
    protected Class<? extends FormInputResponseService> getClassUnderTest() {
        return FormInputResponseServiceImpl.class;
    }
}


