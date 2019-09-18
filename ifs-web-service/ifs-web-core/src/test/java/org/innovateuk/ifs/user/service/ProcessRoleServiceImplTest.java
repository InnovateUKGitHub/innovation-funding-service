package org.innovateuk.ifs.user.service;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.Future;

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
        Long applicationId = 1L;
        List<ProcessRoleResource> resources = Lists.newArrayList(new ProcessRoleResource());
        RestResult<ProcessRoleResource[]> restResult = restSuccess(resources.toArray(new ProcessRoleResource[resources.size()]));
        Future<RestResult<ProcessRoleResource[]>> arrayFuture = mock(Future.class);
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
        Long id = 1L;
        ProcessRoleResource resource = new ProcessRoleResource();
        RestResult<ProcessRoleResource> restResult = restSuccess(resource);
        Future<RestResult<ProcessRoleResource>> future = mock(Future.class);
        when(future.get()).thenReturn(restResult);
        when(userRestService.findProcessRoleById(id)).thenReturn(future);

        Future<ProcessRoleResource> returnedResponse = service.getById(id);
        ProcessRoleResource actualResources = returnedResponse.get();

        verify(userRestService, times(1)).findProcessRoleById(id);
        verifyNoMoreInteractions(userRestService);
        assertEquals(resource, actualResources);
    }
}