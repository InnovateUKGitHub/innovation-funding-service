package com.worth.ifs.project.transactional;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

	private Long projectId = 123L;
	private Long userId = 7L;
	private Long otherUserId = 8L;

	private Application application;
	private Organisation organisation;
	private Role role;
	private User user;
	private ProcessRole processRole;
	private Project project;

	@Before
	public void setUp() {
		organisation = newOrganisation().build();
    	role = newRole().
    			withType(UserRoleType.LEADAPPLICANT).
    			build();
    	user = newUser().
    			withid(userId).
    			build();
    	processRole = newProcessRole().
    			withOrganisation(organisation).
    			withRole(role).
    			withUser(user).
    			build();
    	application = newApplication().
				withId(projectId).
	            withProcessRoles(processRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();
    	project = newProject().withId(projectId).build();

        when(applicationRepositoryMock.findOne(projectId)).thenReturn(application);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
	}
	
    @Test
    public void testCreateProjectFromApplication() {
        ProjectResource newProjectResource = newProjectResource().build();

        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        Project newProjectExpectations = createProjectExpectationsFromOriginalApplication(application);
        Project savedProject = newProject().build();

        when(projectRepositoryMock.save(newProjectExpectations)).thenReturn(savedProject);
        when(projectMapperMock.mapToResource(savedProject)).thenReturn(newProjectResource);

        ServiceResult<ProjectResource> project = service.createProjectFromApplication(123L);
        assertTrue(project.isSuccess());
        assertEquals(newProjectResource, project.getSuccessObject());
    }
    
    @Test
    public void testInvalidProjectManagerProvided() {
        ServiceResult<Void> result = service.setProjectManager(projectId, otherUserId);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("The Project Manager must be a member of the lead partner organisation", result.getFailure().getErrors().get(0).getErrorMessage());
    }
    
    @Test
    public void testValidProjectManagerProvided() {
        ServiceResult<Void> result = service.setProjectManager(projectId, userId);

        assertTrue(result.isSuccess());
    }

    private Project createProjectExpectationsFromOriginalApplication(Application application) {
        return createLambdaMatcher(project -> {
            assertEquals(application.getId(), project.getId());
            assertEquals(application.getName(), project.getName());
            assertEquals(application.getDurationInMonths(), project.getDurationInMonths());
            assertEquals(application.getStartDate(), project.getTargetStartDate());
            assertNull(project.getProjectManager());
            assertNull(project.getAddress());
        });
    }

    @Override
    protected ProjectService supplyServiceUnderTest() {
        return new ProjectServiceImpl();
    }
}
