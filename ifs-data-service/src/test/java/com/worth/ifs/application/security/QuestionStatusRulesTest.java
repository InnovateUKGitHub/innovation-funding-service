package com.worth.ifs.application.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.builder.QuestionStatusResourceBuilder;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.user.builder.ProcessRoleBuilder;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link QuestionStatusRules}
 */
public class QuestionStatusRulesTest extends BasePermissionRulesTest<QuestionStatusRules> {

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Override
    protected QuestionStatusRules supplyPermissionRulesUnderTest() {
        return new QuestionStatusRules();
    }

    @Test
    public void testUserCanReadQuestionStatus() {
        QuestionStatusResource questionStatusResource = QuestionStatusResourceBuilder.newQuestionStatusResource().build();
        ProcessRole processRole = ProcessRoleBuilder.newProcessRole().build();
        UserResource connectedUser = newUserResource().build();
        UserResource notConnectedUser = newUserResource().build();

        when(processRoleRepository.findByUserIdAndApplicationId(connectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(processRole);
        when(processRoleRepository.findByUserIdAndApplicationId(notConnectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(null);

        assertTrue(rules.userCanReadQuestionStatus(questionStatusResource, connectedUser));
        assertFalse(rules.userCanReadQuestionStatus(questionStatusResource, notConnectedUser));
    }

    @Test
    public void testUserCanUpdateQuestionStatus() {
        QuestionStatusResource questionStatusResource = QuestionStatusResourceBuilder.newQuestionStatusResource().build();

        UserResource leadApplicant = newUserResource().build();
        UserResource allowedAndConnectedUser = newUserResource().build();
        UserResource connectedUserAndNotAllowedUser = newUserResource().build();

        ProcessRole leadApplicantProcessRole = ProcessRoleBuilder.newProcessRole().withRole(UserRoleType.LEADAPPLICANT).build();
        when(processRoleRepository.findByUserIdAndApplicationId(leadApplicant.getId(), questionStatusResource.getApplication()))
                .thenReturn(leadApplicantProcessRole);

        ProcessRole allowedProccesRole = ProcessRoleBuilder.newProcessRole().withRole(UserRoleType.APPLICANT).build();
        when(processRoleRepository.findByUserIdAndApplicationId(allowedAndConnectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(allowedProccesRole);
        when(questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionStatusResource.getQuestion(), questionStatusResource.getApplication(), allowedProccesRole.getId()))
                .thenReturn(mock(QuestionStatus.class));
        when(questionRepository.findOne(questionStatusResource.getQuestion()))
                .thenReturn(QuestionBuilder.newQuestion().withMultipleStatuses(false).build());

        ProcessRole connectedProcessRole = ProcessRoleBuilder.newProcessRole().withRole(UserRoleType.APPLICANT).build();
        when(processRoleRepository.findByUserIdAndApplicationId(connectedUserAndNotAllowedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(connectedProcessRole);

        assertTrue(rules.userCanUpdateQuestionStatus(questionStatusResource, leadApplicant));
        assertTrue(rules.userCanUpdateQuestionStatus(questionStatusResource, allowedAndConnectedUser));
        assertFalse(rules.userCanUpdateQuestionStatus(questionStatusResource, connectedUserAndNotAllowedUser));
    }
}
