package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
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
