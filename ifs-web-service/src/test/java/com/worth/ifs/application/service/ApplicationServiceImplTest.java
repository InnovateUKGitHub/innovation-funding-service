package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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
        when(applicationRestService.getApplicationsByUserId(userId)).thenReturn(applications);
        when(applicationRestService.getCompleteQuestionsPercentage(applications.get(0).getId())).thenReturn(20.5d);
        for(ApplicationStatusResource status : statuses) {
            when(applicationStatusRestService.getApplicationStatusById(status.getId())).thenReturn(status);
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
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(applications.get(0));

        ApplicationResource returnedApplication = service.getById(applicationId);
        assertEquals(applications.get(0).getId(), returnedApplication.getId());
    }
    @Test
    public void testGetByIdNotFound() throws Exception {
        Long applicationId = 5L;
        ApplicationResource returnedApplication = service.getById(applicationId);
        assertEquals(null, returnedApplication);
    }

    @Test
    public void testGetByIdNullValue() throws Exception {
        Long applicationId = null;
        ApplicationResource returnedApplication = service.getById(applicationId);
        assertEquals(null, returnedApplication);
    }


    @Test
    public void testGetInProgress() throws Exception {
        List<ApplicationResource> returnedApplications = service.getInProgress(userId);
        returnedApplications.stream().forEach(a ->
                assertThat(applicationStatusRestService.getApplicationStatusById(a.getApplicationStatus()).getName(), Matchers.either(Matchers.is("submitted")).or(Matchers.is("created")))
                );
    }

    @Test
    public void testGetFinished() throws Exception {
        List<ApplicationResource> returnedApplications = service.getFinished(userId);
        returnedApplications.stream().forEach(a ->
                        assertThat(applicationStatusRestService.getApplicationStatusById(a.getApplicationStatus()).getName(), Matchers.either(Matchers.is("approved")).or(Matchers.is("rejected")))
        );
    }
    @Test
     public void testGetProgress() throws Exception {
        Map<Long, Integer> progress = service.getProgress(userId);
        assertEquals(20, progress.get(applications.get(0).getId()).intValue(), 0d);
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
        assertEquals(20, service.getCompleteQuestionsPercentage(applications.get(0).getId()));
    }

    @Test
    public void testSave() throws Exception {
        service.save(applications.get(0));
        Mockito.inOrder(applicationRestService).verify(applicationRestService, calls(1)).saveApplication(applications.get(0));
    }


}