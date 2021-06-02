package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.ExternalFinanceRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSecurityHelperTest {

    @InjectMocks
    private ApplicationSecurityHelper helper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private ExternalFinanceRepository externalFinanceRepository;

    @Mock
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private StakeholderRepository stakeholderRepository;

    @Mock
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Test
    public void canViewApplication() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        Project project = newProject().withApplication(application).build();
        when(projectRepository.findByApplicationId(application.getId())).thenReturn(Optional.of(project));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(project);

        UserResource internalUser = newUserResource().withRoleGlobal(Role.PROJECT_FINANCE).build();
        UserResource applicantMember = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        UserResource applicantNotMember = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        UserResource assessorLinkedToApplication = newUserResource().withRoleGlobal(Role.ASSESSOR).build();
        UserResource assessorNotLinkedToApplication = newUserResource().withRoleGlobal(Role.ASSESSOR).build();
        UserResource externalFinanceLinkedToCompetition = newUserResource().withRoleGlobal(Role.EXTERNAL_FINANCE).build();
        UserResource externalFinanceNotLinkedToCompetition = newUserResource().withRoleGlobal(Role.EXTERNAL_FINANCE).build();
        UserResource monitoringOfficerLinkedToProject = newUserResource().withRoleGlobal(Role.MONITORING_OFFICER).build();
        UserResource monitoringOfficerNotLinkedToProject = newUserResource().withRoleGlobal(Role.MONITORING_OFFICER).build();
        UserResource stakeHolderLinkedToCompetition = newUserResource().withRoleGlobal(Role.STAKEHOLDER).build();
        UserResource stakeHolderNotLinkedToCompetition = newUserResource().withRoleGlobal(Role.STAKEHOLDER).build();
        UserResource ktaLinkedToCompetition = newUserResource().withRoleGlobal(Role.KNOWLEDGE_TRANSFER_ADVISER).build();
        UserResource ktaNotLinkedToCompetition = newUserResource().withRoleGlobal(Role.KNOWLEDGE_TRANSFER_ADVISER).build();
        UserResource supporterLinkedToApplication = newUserResource().withRoleGlobal(Role.SUPPORTER).build();
        UserResource supporterNotLinkedToApplication = newUserResource().withRoleGlobal(Role.SUPPORTER).build();
        UserResource applicantLinkedToProjectNotApplication = newUserResource().withRoleGlobal(Role.APPLICANT).build();

        when(processRoleRepository.existsByUserIdAndApplicationId(anyLong(), eq(application.getId()))).thenReturn(false);
        when(processRoleRepository.existsByUserIdAndApplicationId(applicantMember.getId(), application.getId())).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationId(assessorLinkedToApplication.getId(), application.getId())).thenReturn(true);
        when(processRoleRepository.existsByUserIdAndApplicationId(ktaLinkedToCompetition.getId(), application.getId())).thenReturn(true);

        when(externalFinanceRepository.existsByCompetitionIdAndUserId(eq(competition.getId()), anyLong())).thenReturn(false);
        when(externalFinanceRepository.existsByCompetitionIdAndUserId(competition.getId(), externalFinanceLinkedToCompetition.getId())).thenReturn(true);

        when(monitoringOfficerRepository.existsByProjectIdAndUserId(eq(project.getId()), anyLong())).thenReturn(false);
        when(monitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), monitoringOfficerLinkedToProject.getId())).thenReturn(true);

        when(stakeholderRepository.existsByCompetitionIdAndUserId(eq(competition.getId()), anyLong())).thenReturn(false);
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), stakeHolderLinkedToCompetition.getId())).thenReturn(true);

        when(supporterAssignmentRepository.existsByParticipantIdAndTargetId(anyLong(), eq(application.getId()))).thenReturn(false);
        when(supporterAssignmentRepository.existsByParticipantIdAndTargetId(supporterLinkedToApplication.getId(), application.getId())).thenReturn(true);

        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(eq(project.getId()), anyLong(), eq(new ArrayList<>(PROJECT_USER_ROLES)))).thenReturn(Collections.emptyList());
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), applicantLinkedToProjectNotApplication.getId(), new ArrayList<>(PROJECT_USER_ROLES))).thenReturn(newProjectUser().build(1));

        assertThat(helper.canViewApplication(application.getId(), internalUser), is(true));

        assertThat(helper.canViewApplication(application.getId(), applicantMember), is(true));
        assertThat(helper.canViewApplication(application.getId(), applicantNotMember), is(false));

        assertThat(helper.canViewApplication(application.getId(), assessorLinkedToApplication), is(true));
        assertThat(helper.canViewApplication(application.getId(), assessorNotLinkedToApplication), is(false));

        assertThat(helper.canViewApplication(application.getId(), externalFinanceLinkedToCompetition), is(true));
        assertThat(helper.canViewApplication(application.getId(), externalFinanceNotLinkedToCompetition), is(false));

        assertThat(helper.canViewApplication(application.getId(), monitoringOfficerLinkedToProject), is(true));
        assertThat(helper.canViewApplication(application.getId(), monitoringOfficerNotLinkedToProject), is(false));

        assertThat(helper.canViewApplication(application.getId(), stakeHolderLinkedToCompetition), is(true));
        assertThat(helper.canViewApplication(application.getId(), stakeHolderNotLinkedToCompetition), is(false));

        assertThat(helper.canViewApplication(application.getId(), ktaLinkedToCompetition), is(true));
        assertThat(helper.canViewApplication(application.getId(), ktaNotLinkedToCompetition), is(false));

        assertThat(helper.canViewApplication(application.getId(), supporterLinkedToApplication), is(true));
        assertThat(helper.canViewApplication(application.getId(), supporterNotLinkedToApplication), is(false));

        assertThat(helper.canViewApplication(application.getId(), applicantLinkedToProjectNotApplication), is(true));
    }
}