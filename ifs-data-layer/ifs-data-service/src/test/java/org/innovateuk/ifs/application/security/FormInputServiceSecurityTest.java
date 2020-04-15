package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.application.transactional.FormInputResponseServiceImpl;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.transactional.ApplicationServiceSecurityTest.verifyApplicationRead;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

/**
 * Testing how the secured methods in {@link FormInputService} interact with Spring Security
 */
public class FormInputServiceSecurityTest extends BaseServiceSecurityTest<FormInputResponseService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    private FormInputResponsePermissionRules formInputResponsePermissionRules;
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        formInputResponsePermissionRules = getMockPermissionRulesBean(FormInputResponsePermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testFindResponsesByApplication() {
        verifyApplicationRead(applicationLookupStrategy, applicationRules,
                (applicationId) -> classUnderTest.findResponsesByApplication(applicationId));
    }
    @Test
    public void testFindResponsesByFormInputIdAndApplicationId() {
        long formInputResponseId = 2L;
        verifyApplicationRead(applicationLookupStrategy, applicationRules,
                (applicationId) -> classUnderTest.findResponsesByFormInputIdAndApplicationId(formInputResponseId, applicationId));
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
        verifyApplicationRead(applicationLookupStrategy, applicationRules,
                (applicationId) -> classUnderTest.findResponseByApplicationIdAndQuestionSetupType(applicationId, PROJECT_SUMMARY));
    }

    @Test
    public void findResponseByApplicationIdAndQuestionId() {
        verifyApplicationRead(applicationLookupStrategy, applicationRules,
                (applicationId) -> classUnderTest.findResponseByApplicationIdAndQuestionId(applicationId, 5L));
    }

    @Override
    protected Class<? extends FormInputResponseService> getClassUnderTest() {
        return FormInputResponseServiceImpl.class;
    }
}


