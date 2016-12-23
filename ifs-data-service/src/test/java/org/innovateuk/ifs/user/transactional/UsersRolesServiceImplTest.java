package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UsersRolesServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private UsersRolesService usersRolesService = new UsersRolesServiceImpl();

    @Test
    public void getProcessRoleById() {

        ProcessRole processRole = newProcessRole().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().build();

        when(processRoleRepositoryMock.findOne(1L)).thenReturn(processRole);
        when(processRoleMapperMock.mapToResource(same(processRole))).thenReturn(processRoleResource);

        ServiceResult<ProcessRoleResource> result = usersRolesService.getProcessRoleById(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResource, result.getSuccessObject());

        verify(processRoleRepositoryMock, only()).findOne(1L);
    }
}
