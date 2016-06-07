package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.time.LocalDate;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

    @Test
    public void testCreateProjectFromApplication() {

        Application application = newApplication().
                withId(123L).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

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
    public void testUpdateProjectStartDate() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isSuccess());

        verify(projectRepositoryMock).findOne(123L);
        assertEquals(validDate, existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButProjectDoesntExist() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(projectRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(Project.class, 123L)));
    }

    @Test
    public void testUpdateProjectStartDateButStartDateDoesntBeginOnFirstDayOfMonth() {

        LocalDate now = LocalDate.now();
        LocalDate dateNotOnFirstDayOfMonth = LocalDate.of(now.getYear(), now.getMonthValue(), 2).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, dateNotOnFirstDayOfMonth);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButStartDateNotInFuture() {

        LocalDate now = LocalDate.now();
        LocalDate pastDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, pastDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateFinanceContact() {
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        Role collaboratorRole = newRole().withType(COLLABORATOR).build();

        assertUpdateFinanceSuccessful(leadApplicantRole);
        assertUpdateFinanceSuccessful(collaboratorRole);
    }

    @Test
    public void testUpdateFinanceContactButUserIsNotApplicantOrCollaborator() {

        Role assessorRole = newRole().withType(ASSESSOR).build();

        Application application = newApplication().withId(123L).build();
        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();
        newProcessRole().withOrganisation(organisation).withUser(user).withApplication(application).withRole(assessorRole).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_LEAD_APPLICANT_OR_COLLABORATOR_ON_THE_APPLICATION_FOR_THE_ORGANISATION));

        verify(processRoleRepositoryMock, never()).save(isA(ProcessRole.class));
    }

    @Test
    public void testUpdateFinanceContactWhenNotPresentOnTheApplication() {

        long userIdForUserNotOnProject = 6L;

        Application application = newApplication().withId(123L).build();

        Project existingProject = newProject().withId(123L).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);
        
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();
        newProcessRole().withOrganisation(organisation).withUser(user).withApplication(application).build();
        
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, userIdForUserNotOnProject);
        
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_APPLICATION_FOR_THE_ORGANISATION));
    }

    private void assertUpdateFinanceSuccessful(Role usersRoleOnApplication) {

        Application application = newApplication().withId(123L).build();
        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();
        newProcessRole().withOrganisation(organisation).withUser(user).withApplication(application).withRole(usersRoleOnApplication).build();
        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();

        ProcessRole expectedProcessRole = newProcessRole().
                withOrganisation(organisation).
                withApplication(application).
                withUser(user).
                withRole(financeContactRole).
                build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);
        when(roleRepositoryMock.findByName(FINANCE_CONTACT.getName())).thenReturn(singletonList(financeContactRole));
        when(processRoleRepositoryMock.save(createProcessRoleExpectations(expectedProcessRole))).thenReturn(expectedProcessRole);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isSuccess());

        verify(processRoleRepositoryMock).save(createProcessRoleExpectations(expectedProcessRole));
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

    private ProcessRole createProcessRoleExpectations(ProcessRole expectedProcessRole) {

        return createLambdaMatcher(processRole -> {
            assertEquals(expectedProcessRole.getApplication().getId(), processRole.getApplication().getId());
            assertEquals(expectedProcessRole.getOrganisation().getId(), processRole.getOrganisation().getId());
            assertEquals(expectedProcessRole.getRole().getId(), processRole.getRole().getId());
            assertEquals(expectedProcessRole.getUser().getId(), processRole.getUser().getId());
        });
    }

    @Override
    protected ProjectService supplyServiceUnderTest() {
        return new ProjectServiceImpl();
    }
}
