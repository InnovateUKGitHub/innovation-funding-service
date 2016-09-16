package com.worth.ifs.project.workflow.projectdetails;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.project.workflow.projectdetails.actions.BaseProjectDetailsAction;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowService;
import com.worth.ifs.workflow.BaseWorkflowIntegrationTest;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.function.Function;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;
import static com.worth.ifs.workflow.resource.State.PENDING;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectDetailsWorkflowServiceIntegrationTest extends BaseWorkflowIntegrationTest<BaseProjectDetailsAction> {

    @Autowired
    private ProjectDetailsWorkflowService projectDetailsWorkflowService;

    private ActivityStateRepository activityStateRepositoryMock;
    private ProjectDetailsProcessRepository projectDetailsProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        projectDetailsProcessRepositoryMock = (ProjectDetailsProcessRepository) mockSupplier.apply(ProjectDetailsProcessRepository.class);
    }

    @Test
    public void testAddProjectDetailsUntilAllProjectDetailsSupplied() throws Exception {

        ProjectUser projectUser = newProjectUser().
                withId(789L).
                build();

        Project project = newProject().withId(123L).
                withApplication().
                build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, PENDING);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, PENDING)).thenReturn(pendingState);
        when(projectDetailsProcessRepositoryMock.save(newProjectDetailsProcessExpectations(123L, 789L, ProjectDetailsState.PENDING))).thenReturn(null);

        assertTrue(projectDetailsWorkflowService.projectCreated(project, projectUser));
        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, PENDING);
        verify(projectDetailsProcessRepositoryMock).save(newProjectDetailsProcessExpectations(123L, 789L, ProjectDetailsState.PENDING));
    }

    private ProjectDetailsProcess newProjectDetailsProcessExpectations(Long expectedProjectId, Long expectedProjectUserId, ProjectDetailsState expectedState) {
        return createLambdaMatcher(process -> {
            assertEquals(expectedProjectId, process.getTarget().getId());
            assertEquals(expectedProjectUserId, process.getParticipant().getId());
            assertEquals(expectedState, process.getActivityState());
        });
    }

    @Override
    protected Class getBaseActionType() {
        return BaseProjectDetailsAction.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        return asList(ActivityStateRepository.class, ProjectDetailsProcessRepository.class);
    }
}