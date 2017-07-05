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
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
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

    @Test
    public void testSupportUserCanReadQuestionStatus() {
        QuestionStatusResource questionStatusResource = QuestionStatusResourceBuilder.newQuestionStatusResource().build();

        UserResource supportUser = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.SUPPORT).build())).build();
        UserResource nonSupportUser = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build();

        assertTrue(rules.supportCanReadQuestionStatus(questionStatusResource, supportUser));
        assertFalse(rules.supportCanReadQuestionStatus(questionStatusResource, nonSupportUser));
    }

    @Test
    public void testInnovationLeadUserCanReadQuestionStatus() {
        QuestionStatusResource questionStatusResource = QuestionStatusResourceBuilder.newQuestionStatusResource().build();

        UserResource innovationLeadUser = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_TECHNOLOGIST).build())).build();
        UserResource nonInnovationLeadUser = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build();

        assertTrue(rules.innovationLeadCanReadQuestionStatus(questionStatusResource, innovationLeadUser));
        assertFalse(rules.innovationLeadCanReadQuestionStatus(questionStatusResource, nonInnovationLeadUser));
    }
}
