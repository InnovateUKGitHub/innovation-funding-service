package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.state.transactional.ProjectStateService;
import org.innovateuk.ifs.project.state.transactional.ProjectStateServiceImpl;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_CANNOT_BE_WITHDRAWN;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectStateServiceImplTest extends BaseServiceUnitTest<ProjectStateService> {

    @Mock
    private ProjectWorkflowHandler projectWorkflowHandlerMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Test
    public void withdrawProject() {
        Long projectId = 123L;
        Long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(IFS_ADMINISTRATOR)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(projectWorkflowHandlerMock.projectWithdrawn(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.withdrawProject(projectId);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).projectWithdrawn(eq(project), any());
    }

    @Test
    public void withdrawProject_fails() {
        Long projectId = 321L;
        Long userId = 987L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(IFS_ADMINISTRATOR)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        setLoggedInUser(loggedInUser);
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(projectWorkflowHandlerMock.projectWithdrawn(eq(project), any())).thenReturn(false);

        ServiceResult<Void> result = service.withdrawProject(projectId);
        assertTrue(result.isFailure());
        assertEquals(PROJECT_CANNOT_BE_WITHDRAWN.getErrorKey(), result.getErrors().get(0).getErrorKey());
        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).projectWithdrawn(eq(project), any());
    }

    @Test
    public void withdrawProject_cannotFindIdFails() {
        Long projectId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource user = newUserResource()
                .withRoleGlobal(IFS_ADMINISTRATOR)
                .build();
        setLoggedInUser(user);
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        when(projectWorkflowHandlerMock.projectWithdrawn(eq(project), any())).thenReturn(false);

        ServiceResult<Void> result = service.withdrawProject(projectId);
        assertTrue(result.isFailure());
        verify(projectRepositoryMock).findById(projectId);
        verifyZeroInteractions(projectWorkflowHandlerMock);
    }

    @Test
    public void handleProjectOffline() {
        Long projectId = 123L;
        Long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(IFS_ADMINISTRATOR)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.handleProjectOffline(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.handleProjectOffline(projectId);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).handleProjectOffline(eq(project), any());
    }

    @Test
    public void completeProjectOffline() {
        Long projectId = 123L;
        Long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(IFS_ADMINISTRATOR)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.completeProjectOffline(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.completeProjectOffline(projectId);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).completeProjectOffline(eq(project), any());
    }


    @Override
    protected ProjectStateService supplyServiceUnderTest() {
        return new ProjectStateServiceImpl();
    }
}
