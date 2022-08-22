package org.innovateuk.ifs.project.state.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_CANNOT_BE_WITHDRAWN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
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

    @Mock
    private ProjectStateCommentsService projectStateCommentsService;

    @Mock
    private ProjectDetailsService projectDetailsServiceMock;

    @Test
    public void withdrawProject() {
        long projectId = 123L;
        long userId = 456L;
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
        verify(projectStateCommentsService).create(projectId, ProjectState.WITHDRAWN);
    }

    @Test
    public void withdrawProject_fails() {
        long projectId = 321L;
        long userId = 987L;
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
        verifyNoInteractions(projectStateCommentsService);
    }

    @Test
    public void withdrawProject_cannotFindIdFails() {
        long projectId = 456L;
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
        verifyNoInteractions(projectWorkflowHandlerMock);
        verifyNoInteractions(projectStateCommentsService);
    }

    @Test
    public void handleProjectOffline() {
        long projectId = 123L;
        long userId = 456L;
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
        verify(projectStateCommentsService).create(projectId, ProjectState.HANDLED_OFFLINE);
    }

    @Test
    public void completeProjectOffline() {
        long projectId = 123L;
        long userId = 456L;
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
        verify(projectStateCommentsService).create(projectId, ProjectState.COMPLETED_OFFLINE);
    }

    @Test
    public void putProjectOnHold() {
        long projectId = 123L;
        long userId = 456L;
        OnHoldReasonResource onHoldReasonResource = new OnHoldReasonResource("Title", "Body");
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(PROJECT_FINANCE)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.putProjectOnHold(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.putProjectOnHold(projectId, onHoldReasonResource);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).putProjectOnHold(eq(project), any());
        verify(projectStateCommentsService).create(projectId, ProjectState.ON_HOLD, onHoldReasonResource);
    }

    @Test
    public void resumeProject() {
        long projectId = 123L;
        long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(PROJECT_FINANCE)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.resumeProject(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.resumeProject(projectId);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).resumeProject(eq(project), any());
        verify(projectStateCommentsService).create(projectId, ProjectState.SETUP);
    }


    @Test
    public void markAsSuccessful() {
        long projectId = 123L;
        long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(PROJECT_FINANCE)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.markAsSuccessful(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.markAsSuccessful(projectId);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).markAsSuccessful(eq(project), any());
        verify(projectStateCommentsService).create(projectId, ProjectState.LIVE);
    }

    @Test
    public void markAsUnsuccessful() {
        long projectId = 123L;
        long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(PROJECT_FINANCE)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.markAsUnsuccessful(eq(project), any())).thenReturn(true);

        ServiceResult<Void> result = service.markAsUnsuccessful(projectId);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).markAsUnsuccessful(eq(project), any());
        verify(projectStateCommentsService).create(projectId, ProjectState.UNSUCCESSFUL);
    }

    @Test
    public void markLoansProjectAsSuccessful() {
        long projectId = 123L;
        long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(PROJECT_FINANCE)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);

        LocalDate now = LocalDate.now();
        LocalDate projectStartDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.markAsSuccessful(eq(project), any())).thenReturn(true);
        when(projectDetailsServiceMock.updateLoansProjectSetupCompleteDate(projectId)).thenReturn(serviceSuccess());
        when(projectDetailsServiceMock.updateProjectStartDate(projectId, projectStartDate)).thenReturn(serviceSuccess());
        when(projectDetailsServiceMock.updateLoansProjectSetupCompleteDate(projectId)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.markAsSuccessful(projectId, projectStartDate);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).markAsSuccessful(eq(project), any());
        verify(projectDetailsServiceMock).updateProjectStartDate(projectId,projectStartDate);
        verify(projectDetailsServiceMock).updateLoansProjectSetupCompleteDate(projectId);
        verify(projectStateCommentsService).create(projectId, ProjectState.LIVE);
    }

    //TODO currentlu master build is failing so need to test afer.
    @Test
    public void markLoansProjectAsSuccessful_UnAuthorisedUser() {
        long projectId = 123L;
        long userId = 456L;
        Project project = newProject().withId(projectId).build();
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(ASSESSOR)
                .withId(userId)
                .build();
        User user = newUser()
                .withId(userId)
                .build();
        setLoggedInUser(loggedInUser);

        LocalDate now = LocalDate.now();
        LocalDate projectStartDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        when(projectWorkflowHandlerMock.markAsSuccessful(eq(project), any())).thenReturn(true);
        when(projectDetailsServiceMock.updateLoansProjectSetupCompleteDate(projectId)).thenReturn(serviceSuccess());
        when(projectDetailsServiceMock.updateProjectStartDate(projectId, projectStartDate)).thenReturn(serviceSuccess());
        when(projectDetailsServiceMock.updateLoansProjectSetupCompleteDate(projectId)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.markAsSuccessful(projectId, projectStartDate);
        assertTrue(result.isSuccess());

        verify(projectRepositoryMock).findById(projectId);
        verify(userRepositoryMock).findById(userId);
        verify(projectWorkflowHandlerMock).markAsSuccessful(eq(project), any());
        verify(projectDetailsServiceMock).updateProjectStartDate(projectId,projectStartDate);
        verify(projectDetailsServiceMock).updateLoansProjectSetupCompleteDate(projectId);
        verify(projectStateCommentsService).create(projectId, ProjectState.LIVE);
    }

    @Override
    protected ProjectStateService supplyServiceUnderTest() {
        return new ProjectStateServiceImpl();
    }
}
