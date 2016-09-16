package com.worth.ifs.project.workflow.projectdetails;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.project.workflow.projectdetails.actions.BaseProjectDetailsAction;
import com.worth.ifs.project.workflow.projectdetails.actions.ProjectCreatedAction;
import com.worth.ifs.project.workflow.projectdetails.actions.ReadyToSubmitProjectDetailsAction;
import com.worth.ifs.project.workflow.projectdetails.actions.SubmitProjectDetailsAction;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;
import static com.worth.ifs.workflow.resource.State.PENDING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@DirtiesContext
public class ProjectDetailsWorkflowServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ProjectCreatedAction projectCreatedAction;

    @Autowired
    private ReadyToSubmitProjectDetailsAction readyToSubmitProjectDetailsAction;

    @Autowired
    private SubmitProjectDetailsAction submitProjectDetailsAction;

    @Autowired
    private ProjectDetailsWorkflowService projectDetailsWorkflowService;

    private ActivityStateRepository activityStateRepositoryMock;

    private ProjectDetailsProcessRepository projectDetailsProcessRepositoryMock;

    @Before
    public void swapOutRepositories() {
        activityStateRepositoryMock = Mockito.mock(ActivityStateRepository.class);
        projectDetailsProcessRepositoryMock = Mockito.mock(ProjectDetailsProcessRepository.class);
        setMockRepositoriesOnAction(projectCreatedAction);
        setMockRepositoriesOnAction(readyToSubmitProjectDetailsAction);
        setMockRepositoriesOnAction(submitProjectDetailsAction);
    }

    @Test
    public void testAddProjectDetailsUntilAllProjectDetailsSupplied() throws Exception {

        Organisation organisation1 = newOrganisation().build();

        List<ProjectUser> projectUsers = newProjectUser().
                withId(456L, 789L, 890L).
                withOrganisation(organisation1).
                build(3);

        Project project = newProject().withId(123L).
                withApplication().
                withProjectUsers(projectUsers).
                build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, PENDING);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, PENDING)).thenReturn(pendingState);
        when(projectDetailsProcessRepositoryMock.save(newProjectDetailsProcessExpectations(123L, 789L, ProjectDetailsState.PENDING))).thenReturn(null);

        assertTrue(projectDetailsWorkflowService.projectCreated(project, projectUsers.get(1)));
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

    private void setMockRepositoriesOnAction(BaseProjectDetailsAction action) {
        ReflectionTestUtils.setField(action, "activityStateRepository", activityStateRepositoryMock);
        ReflectionTestUtils.setField(action, "projectDetailsProcessRepository", projectDetailsProcessRepositoryMock);
    }
}