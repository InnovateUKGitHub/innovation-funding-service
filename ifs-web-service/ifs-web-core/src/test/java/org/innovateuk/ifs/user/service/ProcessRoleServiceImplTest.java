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
    private ProcessRoleRestService processRoleRestService;

    protected ProcessRoleService supplyServiceUnderTest() { return new ProcessRoleServiceImpl(); }

    @Test
    public void findAssignableProcessRoles() throws Exception {
        long applicationId = 1;
        List<ProcessRoleResource> resources = newArrayList(new ProcessRoleResource());
        RestResult<List<ProcessRoleResource>> restResult = restSuccess(resources);
        when(processRoleRestService.findAssignableProcessRoles(applicationId)).thenReturn(restResult);

        List<ProcessRoleResource> actualResources = service.findAssignableProcessRoles(applicationId);

        verify(processRoleRestService, times(1)).findAssignableProcessRoles(applicationId);
        verifyNoMoreInteractions(processRoleRestService);
        assertEquals(resources, actualResources);
    }

    @Test
    public void getById() throws Exception {
        long id = 1;
        ProcessRoleResource resource = new ProcessRoleResource();
        RestResult<ProcessRoleResource> restResult = restSuccess(resource);
        Future future = mock(Future.class);
        when(future.get()).thenReturn(restResult);
        when(processRoleRestService.findProcessRoleById(id)).thenReturn(future);

        Future<ProcessRoleResource> returnedResponse = service.getById(id);
        ProcessRoleResource actualResources = returnedResponse.get();

        verify(processRoleRestService, times(1)).findProcessRoleById(id);
        verifyNoMoreInteractions(processRoleRestService);
        assertEquals(resource, actualResources);
    }
}