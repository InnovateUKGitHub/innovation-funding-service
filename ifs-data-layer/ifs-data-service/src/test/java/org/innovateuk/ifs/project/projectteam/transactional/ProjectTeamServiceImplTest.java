package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectTeamServiceImplTest extends BaseServiceUnitTest<ProjectTeamService> {

    @Override
    protected ProjectTeamService supplyServiceUnderTest() {
        ProjectTeamServiceImpl projectTeamService = new ProjectTeamServiceImpl();
        return projectTeamService;
    }

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void removeUser() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(Collections.singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(userToRemove)
                .build();

        Project project = newProject().withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepository.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepository).findById(project.getId());
        verify(projectUserRepository).findByProjectIdAndUserId(project.getId(), userToRemove.getId());

        assertTrue(project.getProjectUsers().isEmpty());
    }

    @Test
    public void removeUserFailsFinanceContact() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(Collections.singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(userToRemove)
                .build();

        Project project = newProject().withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepository.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(
                singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepository).findById(project.getId());
        verifyZeroInteractions(projectUserRepository);

        assertTrue(project.getProjectUsers().contains(projectUserToRemove));
    }

    @Test
    public void removeUserFailsProjectManager() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(Collections.singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(userToRemove)
                .build();

        Project project = newProject().withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepository.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(
                singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepository).findById(project.getId());
        verifyZeroInteractions(projectUserRepository);

        assertTrue(project.getProjectUsers().contains(projectUserToRemove));
    }
}
