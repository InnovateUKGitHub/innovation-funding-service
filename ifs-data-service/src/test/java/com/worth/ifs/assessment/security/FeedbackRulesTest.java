package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for permissions checking around the management of Assessor Feedback, and who can and can't perform
 * actions upon it.
 */
public class FeedbackRulesTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FeedbackRules rules = new FeedbackRules();

    @Test
    public void test_assessorCanReadTheirOwnFeedback() {

        User user = newUser().build();
        Role assessorRole = newRole().withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().withUser(user).withRole(assessorRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(assessorProcessRole.getId());

        when(processRoleRepositoryMock.findOne(assessorProcessRole.getId())).thenReturn(assessorProcessRole);

        assertTrue(rules.assessorCanReadTheirOwnFeedback(feedback, user));
    }

    @Test
    public void test_assessorCanReadTheirOwnFeedback_doesntBelongToThisUser() {

        User user = newUser().build();
        User differentUser = newUser().build();
        Role assessorRole = newRole().withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().withUser(differentUser).withRole(assessorRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(assessorProcessRole.getId());

        when(processRoleRepositoryMock.findOne(assessorProcessRole.getId())).thenReturn(assessorProcessRole);

        assertFalse(rules.assessorCanReadTheirOwnFeedback(feedback, user));
    }

    @Test
    public void test_assessorCanReadTheirOwnFeedback_wrongProcessRoleType_shouldNeverHappen() {

        User user = newUser().build();
        Role applicantRole = newRole().withType(APPLICANT).build();
        ProcessRole applicantProcessRole = newProcessRole().withUser(user).withRole(applicantRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(applicantProcessRole.getId());

        when(processRoleRepositoryMock.findOne(applicantProcessRole.getId())).thenReturn(applicantProcessRole);

        assertFalse(rules.assessorCanReadTheirOwnFeedback(feedback, user));
    }

    @Test
    public void test_assessorCanUpdateTheirOwnFeedback() {

        User user = newUser().build();
        Role assessorRole = newRole().withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().withUser(user).withRole(assessorRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(assessorProcessRole.getId());

        when(processRoleRepositoryMock.findOne(assessorProcessRole.getId())).thenReturn(assessorProcessRole);

        assertTrue(rules.assessorCanUpdateTheirOwnFeedback(feedback, user));
    }

    @Test
    public void test_assessorCanUpdateTheirOwnFeedback_doesntBelongToThisUser() {

        User user = newUser().build();
        User differentUser = newUser().build();
        Role assessorRole = newRole().withType(ASSESSOR).build();
        ProcessRole assessorProcessRole = newProcessRole().withUser(differentUser).withRole(assessorRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(assessorProcessRole.getId());

        when(processRoleRepositoryMock.findOne(assessorProcessRole.getId())).thenReturn(assessorProcessRole);

        assertFalse(rules.assessorCanUpdateTheirOwnFeedback(feedback, user));
    }

    @Test
    public void test_assessorUpdateTheirOwnFeedback_wrongProcessRoleType_shouldNeverHappen() {

        User user = newUser().build();
        Role applicantRole = newRole().withType(APPLICANT).build();
        ProcessRole applicantProcessRole = newProcessRole().withUser(user).withRole(applicantRole).build();

        Feedback feedback = new Feedback();
        feedback.setAssessorProcessRoleId(applicantProcessRole.getId());

        when(processRoleRepositoryMock.findOne(applicantProcessRole.getId())).thenReturn(applicantProcessRole);

        assertFalse(rules.assessorCanUpdateTheirOwnFeedback(feedback, user));
    }
}
