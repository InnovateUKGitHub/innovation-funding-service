package org.innovateuk.ifs.user.service;

import java.util.List;
import java.util.concurrent.Future;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;
import org.mockito.Mock;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Testing FormInputServiceImpl and its interactions with its mock rest service.
 */
public class ProcessRoleServiceImplTest extends BaseServiceUnitTest<ProcessRoleService> {

    @Mock
    private UserRestService userRestService;

    protected ProcessRoleService supplyServiceUnderTest() { return new ProcessRoleServiceImpl(); }

    @Test
    public void findAssignableProcessRoles() throws Exception {
        long applicationId = 1;
        List<ProcessRoleResource> resources = newArrayList(new ProcessRoleResource());
        RestResult<ProcessRoleResource[]> restResult = restSuccess(resources.toArray(new ProcessRoleResource[0]));
        Future arrayFuture = mock(Future.class);
        when(arrayFuture.get()).thenReturn(restResult);
        when(userRestService.findAssignableProcessRoles(applicationId)).thenReturn(arrayFuture);

        Future<List<ProcessRoleResource>> returnedResponse = service.findAssignableProcessRoles(applicationId);
        List<ProcessRoleResource> actualResources = returnedResponse.get();

        verify(userRestService, times(1)).findAssignableProcessRoles(applicationId);
        verifyNoMoreInteractions(userRestService);
        assertEquals(resources, actualResources);
    }

    @Test
    public void getById() throws Exception {
        long id = 1;
        ProcessRoleResource resource = new ProcessRoleResource();
        RestResult<ProcessRoleResource> restResult = restSuccess(resource);
        Future future = mock(Future.class);
        when(future.get()).thenReturn(restResult);
        when(userRestService.findProcessRoleById(id)).thenReturn(future);

        Future<ProcessRoleResource> returnedResponse = service.getById(id);
        ProcessRoleResource actualResources = returnedResponse.get();

        verify(userRestService, times(1)).findProcessRoleById(id);
        verifyNoMoreInteractions(userRestService);
        assertEquals(resource, actualResources);
    }
}