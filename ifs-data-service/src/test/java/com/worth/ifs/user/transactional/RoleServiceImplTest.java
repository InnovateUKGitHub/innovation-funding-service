package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.RoleResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class RoleServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private RoleService roleService = new RoleServiceImpl();

    @Test
    public void findByUserRoleType() throws Exception {

        Role role = newRole().build();
        RoleResource roleResource = newRoleResource().build();

        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.name())).thenReturn(role);
        when(roleMapperMock.mapToResource(same(role))).thenReturn(roleResource);

        ServiceResult<RoleResource> result = roleService.findByUserRoleType(LEADAPPLICANT);
        assertTrue(result.isSuccess());
        assertEquals(roleResource, result.getSuccessObject());

        verify(roleRepositoryMock, only()).findOneByName(LEADAPPLICANT.name());
    }

}