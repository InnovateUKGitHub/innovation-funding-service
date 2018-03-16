package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testing how the secured methods in {@link FormInputService} interact with Spring Security
 */
public class FormInputServiceSecurityTest extends BaseServiceSecurityTest<FormInputResponseService> {

    private FormInputResponsePermissionRules formInputResponsePermissionRules;

    @Before
    public void lookupPermissionRules() {
        formInputResponsePermissionRules = getMockPermissionRulesBean(FormInputResponsePermissionRules.class);
    }

    @Test
    public void testFindResponsesByApplication() {
        long applicationId = 1l;
        classUnderTest.findResponsesByApplication(applicationId);
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).assessorCanSeeTheInputResponsesInApplicationsTheyAssess(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).internalUserCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource.class), isA(UserResource.class));
    }

    @Test
    public void testFindResponsesByFormInputIdAndApplicationId() {
        long applicationId = 1l;
        long formInputResponseId = 2l;
        classUnderTest.findResponsesByFormInputIdAndApplicationId(applicationId, formInputResponseId);
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).assessorCanSeeTheInputResponsesInApplicationsTheyAssess(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).internalUserCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource.class), isA(UserResource.class));
    }


    @Test
    public void testSaveQuestionResponse() {
        final long applicationId = 1l;
        final long formInputId = 2l;
        final long userId = 3l;
        final FormInputResponseCommand formInputResponseCommand = new FormInputResponseCommand(formInputId, applicationId, userId, "test text");
        assertAccessDenied(
                () -> classUnderTest.saveQuestionResponse(formInputResponseCommand),
                () -> verify(formInputResponsePermissionRules).aConsortiumMemberCanUpdateAFormInputResponse(isA(FormInputResponseCommand.class), isA(UserResource.class))
        );
    }

    @Test
    public void findResponseByApplicationIdAndQuestionName() {
        assertAccessDenied(
                () -> classUnderTest.findResponseByApplicationIdAndQuestionName(1L, "name"),
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
        classUnderTest.findResponseByApplicationIdAndQuestionId(1L, 2L);
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .assessorCanSeeTheInputResponsesInApplicationsTheyAssess(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .internalUserCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputResponseService.ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource.class), isA(UserResource.class));
    }

    @Override
    protected Class<TestFormInputResponseService> getClassUnderTest() {
        return TestFormInputResponseService.class;
    }

    public static class TestFormInputResponseService implements FormInputResponseService {

        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

        @Override
        public ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(long applicationId) {
            return serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(long formInputId, long applicationId) {
            return serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<FormInputResponseResource> findResponseByApplicationIdAndQuestionName(long applicationId,
                                                                                                   String questionName) {
            return serviceSuccess(newFormInputResponseResource().build());
        }

        @Override
        public ServiceResult<List<FormInputResponseResource>> findResponseByApplicationIdAndQuestionId(long applicationId, long questionId) {
            return serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<FormInputResponse> saveQuestionResponse(FormInputResponseCommand command) {
            return null;
        }

    }
}


