package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.applicantProcessRoles;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link QuestionStatusRules}
 */
public class QuestionStatusRulesTest extends BasePermissionRulesTest<QuestionStatusRules> {

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Override
    protected QuestionStatusRules supplyPermissionRulesUnderTest() {
        return new QuestionStatusRules();
    }

    @Test
    public void userCanReadQuestionStatus() {
        ApplicationResource application = newApplicationResource().build();
        QuestionStatusResource questionStatusResource = newQuestionStatusResource()
                .withApplication(application).build();
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
    public void userCanUpdateQuestionStatus() {
        ApplicationResource application = newApplicationResource().build();
        QuestionStatusResource questionStatusResource = newQuestionStatusResource()
                .withApplication(application).build();
        UserResource leadApplicant = newUserResource().build();
        UserResource allowedAndConnectedUser = newUserResource().build();
        UserResource connectedUserAndNotAllowedUser = newUserResource().build();

        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(leadApplicant.getId(), questionStatusResource.getApplication(), ProcessRoleType.LEADAPPLICANT))
                .thenReturn(true);

        ProcessRole allowedProccesRole = ProcessRoleBuilder.newProcessRole().withRole(ProcessRoleType.COLLABORATOR).build();
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(allowedAndConnectedUser.getId(), applicantProcessRoles(), questionStatusResource.getApplication()))
                .thenReturn(allowedProccesRole);
        when(processRoleRepository.existsByUserIdAndApplicationIdAndRole(allowedAndConnectedUser.getId(), questionStatusResource.getApplication(), ProcessRoleType.COLLABORATOR))
                .thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationId(allowedAndConnectedUser.getId(), questionStatusResource.getApplication()))
                .thenReturn(true);

        when(questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionStatusResource.getQuestion(), questionStatusResource.getApplication(), allowedProccesRole.getId()))
                .thenReturn(mock(QuestionStatus.class));
        when(questionRepository.findById(questionStatusResource.getQuestion()))
                .thenReturn(Optional.of(QuestionBuilder.newQuestion().withMultipleStatuses(false).build()));

        ProcessRole connectedProcessRole = ProcessRoleBuilder.newProcessRole().withRole(ProcessRoleType.COLLABORATOR).build();
        when(processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(connectedUserAndNotAllowedUser.getId(), applicantProcessRoles(), questionStatusResource.getApplication()))
                .thenReturn(connectedProcessRole);

        assertTrue(rules.userCanUpdateQuestionStatus(questionStatusResource, leadApplicant));
        assertTrue(rules.userCanUpdateQuestionStatus(questionStatusResource, allowedAndConnectedUser));
        assertFalse(rules.userCanUpdateQuestionStatus(questionStatusResource, connectedUserAndNotAllowedUser));
    }

    @Test
    public void internalUserCanReadQuestionStatus() {
        QuestionStatusResource questionStatusResource = newQuestionStatusResource().build();

        UserResource compAdminUser = newUserResource().withRoleGlobal(Role.COMP_ADMIN).build();
        UserResource supportUser = newUserResource().withRoleGlobal(Role.SUPPORT).build();
        UserResource projectFinanceUser = newUserResource().withRoleGlobal(Role.PROJECT_FINANCE).build();
        UserResource innovationLeadUser = newUserResource().withRoleGlobal(Role.INNOVATION_LEAD).build();
        UserResource nonInternalUser = newUserResource().withRoleGlobal(Role.ASSESSOR).build();

        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, innovationLeadUser));
        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, compAdminUser));
        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, supportUser));
        assertTrue(rules.internalUserCanReadQuestionStatus(questionStatusResource, projectFinanceUser));
        assertFalse(rules.internalUserCanReadQuestionStatus(questionStatusResource, nonInternalUser));
    }

    @Test
    public void competitionFinanceUserCanReadQuestionStatus() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        QuestionStatusResource questionStatusResource = newQuestionStatusResource()
                .withApplication(newApplicationResource().withId(application.getId()).build())
                .build();
        UserResource competitionFinanceUser = newUserResource().withRoleGlobal(EXTERNAL_FINANCE).build();

        when(applicationRepository.findById(questionStatusResource.getApplication())).thenReturn(Optional.of(application));
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), competitionFinanceUser.getId())).thenReturn(true);
        assertTrue(rules.competitionFinanceUserCanReadQuestionStatus(questionStatusResource, competitionFinanceUser));
    }

    @Test
    public void onlyMemberOfProjectTeamCanMarkSection() {
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource().build();
        UserResource leadApplicant = UserResourceBuilder.newUserResource().build();
        UserResource nonProjectTeamMember = UserResourceBuilder.newUserResource().build();

        when(processRoleRepository.existsByUserIdAndRoleInAndApplicationId(leadApplicant.getId(), EnumSet.of(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR), application.getId()))
                .thenReturn(true);
        when(processRoleRepository.existsByUserIdAndRoleInAndApplicationId(nonProjectTeamMember.getId(), EnumSet.of(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR), application.getId()))
                .thenReturn(false);

        assertTrue(rules.onlyMemberOfProjectTeamCanMarkSection(application, leadApplicant));
        assertFalse(rules.onlyMemberOfProjectTeamCanMarkSection(application, nonProjectTeamMember));
    }
}
