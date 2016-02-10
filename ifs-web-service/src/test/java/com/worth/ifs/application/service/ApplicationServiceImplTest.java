package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.ApplicationStatusResourceBuilder.newApplicationStatusResource;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;

public class ApplicationServiceImplTest extends BaseServiceUnitTest<ApplicationService> {

    @Mock
    ApplicationRestService applicationRestService;
    @Mock
    ApplicationStatusRestService applicationStatusRestService;

    private List<ApplicationResource> applications;
    private Long userId;


    @Override
    @Before
    public void setUp() {
        super.setUp();

        ApplicationStatusResource[] statuses = newApplicationStatusResource().
                withName("created", "submitted", "something", "finished", "approved", "rejected").
                buildArray(6, ApplicationStatusResource.class);

        applications = newApplicationResource().withApplicationStatus(statuses).build(6);

        userId = 1L;
        when(applicationRestService.getApplicationsByUserId(userId)).thenReturn(restSuccess(applications));
        when(applicationRestService.getCompleteQuestionsPercentage(applications.get(0).getId())).thenReturn(settable(restSuccess(20.5d)));
        for(ApplicationStatusResource status : statuses) {
            when(applicationStatusRestService.getApplicationStatusById(status.getId())).thenReturn(restSuccess(status));
        }

    }

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Test
     public void testGetById() throws Exception {
        Long applicationId = 1L;
        List<ApplicationResource> applications = newApplicationResource().withId(applicationId).build(1);
        applications.get(0).setId(applicationId);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applications.get(0)));

        ApplicationResource returnedApplication = service.getById(applicationId);
        assertEquals(applications.get(0).getId(), returnedApplication.getId());
    }
    @Test
    public void testGetByIdNotFound() throws Exception {
        Long applicationId = 5L;
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restFailure(notFoundError(Application.class)));
        ApplicationResource returnedApplication = service.getById(applicationId);
        assertEquals(null, returnedApplication);
    }

    @Test
    public void testGetByIdNullValue() throws Exception {
        ApplicationResource returnedApplication = service.getById(null);
        assertEquals(null, returnedApplication);
    }


    @Test
    public void testGetInProgress() throws Exception {
        List<ApplicationResource> returnedApplications = service.getInProgress(userId);
        returnedApplications.stream().forEach(a ->
                assertThat(applicationStatusRestService.getApplicationStatusById(a.getApplicationStatus()).getSuccessObject().getName(), Matchers.either(Matchers.is("submitted")).or(Matchers.is("created")))
                );
    }

    @Test
    public void testGetFinished() throws Exception {
        List<ApplicationResource> returnedApplications = service.getFinished(userId);
        returnedApplications.stream().forEach(a ->
                        assertThat(applicationStatusRestService.getApplicationStatusById(a.getApplicationStatus()).getSuccessObject().getName(), Matchers.either(Matchers.is("approved")).or(Matchers.is("rejected")))
        );
    }
    @Test
     public void testGetProgress() throws Exception {
        Map<Long, Integer> progress = service.getProgress(userId);
        assertEquals(20, progress.get(applications.get(0).getId()), 0d);
    }
    @Test
    public void testGetProgressNull() throws Exception {
        Map<Long, Integer> progress = service.getProgress(userId);
        assertNull(progress.get(2L));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Long statusId = 1L;
        service.updateStatus(applications.get(0).getId(), statusId);
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).updateApplicationStatus(applications.get(0).getId(), statusId);
    }

    @Test
    public void testGetCompleteQuestionsPercentage() throws Exception {
        // somehow the progress is rounded, because we use a long as the return type.
        assertEquals(20, service.getCompleteQuestionsPercentage(applications.get(0).getId()).get().intValue());
    }

    @Test
    public void testSave() throws Exception {
        service.save(applications.get(0));
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).saveApplication(applications.get(0));
    }


}