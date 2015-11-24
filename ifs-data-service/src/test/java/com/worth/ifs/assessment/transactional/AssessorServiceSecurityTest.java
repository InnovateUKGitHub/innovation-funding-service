package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.security.FeedbackRules;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.util.Either;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.Either.right;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for testing security annotations on methods in the AssessorService
 */
public class AssessorServiceSecurityTest extends BaseServiceSecurityTest<AssessorService> {

    private FeedbackRules feedbackRules;

    @Before
    public void setup() {
        super.setup();
        feedbackRules = getMockPermissionRulesBean(FeedbackRules.class);
    }

    @Test
    public void test_updateAssessorFeedback() {

        Role assessorRole = newRole().withType(ASSESSOR).build();
        User assessor = newUser().withRolesGlobal(assessorRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(123L);

        setLoggedInUser(assessor);
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, assessor)).thenReturn(true);

        // call the method under test
        assertEquals("Security tested!", service.updateAssessorFeedback(feedback).getRight().getMessage());

        verify(feedbackRules).assessorCanUpdateTheirOwnFeedback(feedback, assessor);
    }

    private static class TestAssessmentService implements AssessorService {

        @Override
        public Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Feedback feedback) {
            return right(new ServiceSuccess("Security tested!"));
        }
    }

    @Override
    protected Class<? extends AssessorService> getServiceClass() {
        return TestAssessmentService.class;
    }
}
