package com.worth.ifs.form.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.*;
import com.worth.ifs.form.transactional.FormInputService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testing how the secured methods in {@link FormInputService} interact with Spring Security
 */
public class FormInputServiceSecurityTest extends BaseServiceSecurityTest<FormInputService> {

    private FormInputResponsePermissionRules formInputResponsePermissionRules;


    @Before
    public void lookupPermissionRules() {
        formInputResponsePermissionRules = getMockPermissionRulesBean(FormInputResponsePermissionRules.class);
    }

    @Test
    public void testFindResponsesByApplication() {
        long applicationId = 1l;
        service.findResponsesByApplication(applicationId);
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).compAdminCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource.class), isA(UserResource.class));
    }

    @Test
    public void testFindResponsesByFormInputIdAndApplicationId() {
        long applicationId = 1l;
        long formInputResponseId = 2l;
        service.findResponsesByFormInputIdAndApplicationId(applicationId, formInputResponseId);
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).compAdminCanSeeFormInputResponsesForApplications(isA(FormInputResponseResource.class), isA(UserResource.class));
        verify(formInputResponsePermissionRules, times(TestFormInputService.ARRAY_SIZE_FOR_POST_FILTER_TESTS)).consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(isA(FormInputResponseResource.class), isA(UserResource.class));
    }


    @Test
    public void testSaveQuestionResponse() {
        final long applicationId = 1l;
        final long formInputId = 2l;
        final long userId = 3l;
        final FormInputResponseCommand formInputResponseCommand = new FormInputResponseCommand(formInputId, applicationId, userId, "test text");
        assertAccessDenied(
                () -> service.saveQuestionResponse(formInputResponseCommand),
                () -> verify(formInputResponsePermissionRules).aConsortiumMemberCanUpdateAFormInputResponse(isA(FormInputResponseCommand.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<TestFormInputService> getServiceClass() {
        return TestFormInputService.class;
    }

    public static class TestFormInputService implements FormInputService {


        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

        @Override
        public ServiceResult<FormInputTypeResource> findFormInputType(Long id) {
            return null;
        }

        @Override
        public ServiceResult<FormInputResource> findFormInput(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<FormInputResource>> findByQuestionId(Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<List<FormInputResource>> findByQuestionIdAndScope(Long questionId, FormInputScope scope) {
            return null;
        }

        @Override
        public ServiceResult<List<FormInputResource>> findByCompetitionId(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<FormInputResource>> findByCompetitionIdAndScope(Long competitionId, FormInputScope scope) {
            return null;
        }

        @Override
        public ServiceResult<List<FormInputResponseResource>> findResponsesByApplication(Long applicationId) {
            return serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<List<FormInputResponseResource>> findResponsesByFormInputIdAndApplicationId(Long formInputId, Long applicationId) {
            return serviceSuccess(newFormInputResponseResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<FormInputResponse> saveQuestionResponse(FormInputResponseCommand command) {
            return null;
        }

        @Override
        public ServiceResult<FormInputResource> save(FormInputResource formInputResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> delete(Long id) {
            return null;
        }
    }
}


