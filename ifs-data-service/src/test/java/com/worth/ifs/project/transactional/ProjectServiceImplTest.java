package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.resource.ProjectResource;
import org.junit.Test;

import java.time.LocalDate;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(projectRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, LocalDate.now());
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
