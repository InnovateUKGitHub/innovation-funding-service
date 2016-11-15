package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.transactional.QuestionAssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.builder.RoleResourceBuilder;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.Arrays;

/**
 * Testing how the secured methods in QuestionService interact with Spring Security
 */
public class QuestionAssessmentServiceSecurityTest extends BaseServiceSecurityTest<QuestionAssessmentService> {

    @Test
    public void testGetById() {
        testRolesAllowed(() -> classUnderTest.getById(1L));
    }

    @Test
    public void testFindByQuestion() {
        testRolesAllowed(() -> classUnderTest.findByQuestion(1L));
    }

    private void testRolesAllowed(Runnable serviceMethodCall) {
        setLoggedInUser(null);

        assertAccessDenied(serviceMethodCall, () -> {
        });

        Arrays.stream(UserRoleType.values()).forEach(userRoleType -> {

            setLoggedInUser(UserResourceBuilder.newUserResource()
                    .withRolesGlobal(RoleResourceBuilder.newRoleResource()
                            .withType(userRoleType).build(2)
                    ).build());

            if (!userRoleType.equals(UserRoleType.COMP_ADMIN)
                    && !userRoleType.equals(UserRoleType.PROJECT_FINANCE)) {
                assertAccessDenied(serviceMethodCall, () -> {
                });
            } else {
                serviceMethodCall.run();
            }

        });

    }

    @Override
    protected Class<TestQuestionAssessmentService> getClassUnderTest() {
        return TestQuestionAssessmentService.class;
    }

    public static class TestQuestionAssessmentService implements QuestionAssessmentService {

        @Override
        public ServiceResult<QuestionAssessmentResource> getById(Long id) {
            return null;
        }

        @Override
        public ServiceResult<QuestionAssessmentResource> findByQuestion(Long questionId) {
            return null;
        }
    }
}

