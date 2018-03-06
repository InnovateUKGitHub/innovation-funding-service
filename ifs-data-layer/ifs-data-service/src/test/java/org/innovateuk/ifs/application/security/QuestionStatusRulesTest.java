package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.QuestionBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
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
        UserResource connectedUser = newUserResource().build();
        UserResource notConnectedUser = newUserResource().build();

        when(processRoleRepository.existsByUserIdAndApplicationId(connectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationId(notConnectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(false);

        assertTrue(rules.userCanReadQuestionStatus(questionStatusResource, connectedUser));
        assertFalse(rules.userCanReadQuestionStatus(questionStatusResource, notConnectedUser));
    }

    @Test
    public void testUserCanUpdateQuestionStatus() {
        QuestionStatusResource questionStatusResource = QuestionStatusResourceBuilder.newQuestionStatusResource().build();

        UserResource leadApplicant = newUserResource().build();
        UserResource allowedAndConnectedUser = newUserResource().build();
        UserResource connectedUserAndNotAllowedUser = newUserResource().build();

        when(processRoleRepository.existsByUserIdAndApplicationIdAndRoleName(leadApplicant.getId(), questionStatusResource.getApplication(), Role.LEADAPPLICANT.getName()))
                .thenReturn(true);

        ProcessRole allowedProccesRole = ProcessRoleBuilder.newProcessRole().withRole(Role.APPLICANT).build();
        when(processRoleRepository.findByUserIdAndApplicationId(allowedAndConnectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(allowedProccesRole);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRoleName(allowedAndConnectedUser.getId(), questionStatusResource.getApplication(), Role.APPLICANT.getName()))
                .thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationId(allowedAndConnectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(true);

        when(questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionStatusResource.getQuestion(), questionStatusResource.getApplication(), allowedProccesRole.getId()))
                .thenReturn(mock(QuestionStatus.class));
        when(questionRepository.findOne(questionStatusResource.getQuestion()))
                .thenReturn(QuestionBuilder.newQuestion().withMultipleStatuses(false).build());

        ProcessRole connectedProcessRole = ProcessRoleBuilder.newProcessRole().withRole(Role.APPLICANT).build();
        when(processRoleRepository.findByUserIdAndApplicationId(connectedUserAndNotAllowedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(connectedProcessRole);

        assertTrue(rules.userCanUpdateQuestionStatus(questionStatusResource, leadApplicant));
        assertTrue(rules.userCanUpdateQuestionStatus(questionStatusResource, allowedAndConnectedUser));
        assertFalse(rules.userCanUpdateQuestionStatus(questionStatusResource, connectedUserAndNotAllowedUser));
    }

    @Test
    public void testInternalUserCanReadQuestionStatus() {
        QuestionStatusResource questionStatusResource = QuestionStatusResourceBuilder.newQuestionStatusResource().build();

        UserResource compAdminUser = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        UserResource supportUser = newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();
        UserResource projectFinanceUser = newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();
        UserResource innovationLeadUser = newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();
        UserResource nonInternalUser = newUserResource().withRolesGlobal(singletonList(Role.ASSESSOR)).build();

        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, innovationLeadUser));
        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, compAdminUser));
        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, supportUser));
        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, projectFinanceUser));
        assertFalse(rules.internalUserCanReadQuestionStatus(questionStatusResource, nonInternalUser));
    }
}
