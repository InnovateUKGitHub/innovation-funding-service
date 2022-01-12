package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RoleProfileStatusRestServiceTest extends BaseRestServiceUnitTest<RoleProfileStatusRestServiceImpl> {

    private String restUrl = "/user/%d/role-profile-status";

    @Override
    protected RoleProfileStatusRestServiceImpl registerRestServiceUnderTest() {
        return new RoleProfileStatusRestServiceImpl();
    }

    @Test
    public void updateUserStatus() {
        long userId = 1L;
        RoleProfileStatusResource resource = new RoleProfileStatusResource();

        setupPutWithRestResultExpectations(format(restUrl, userId), resource);

        assertTrue(service.updateUserStatus(userId, resource).isSuccess());
    }

    @Test
    public void findByUserId() {
        long userId = 1L;
        List<RoleProfileStatusResource> resources = newRoleProfileStatusResource().build(1);

        setupGetWithRestResultExpectations(format(restUrl, userId), new ParameterizedTypeReference<List<RoleProfileStatusResource>>() {}, resources);

        assertEquals(resources, service.findByUserId(userId).getSuccess());
    }

    @Test
    public void findByUserIdAndProfileRole() {
        long userId = 1L;
        ProfileRole role = ProfileRole.ASSESSOR;
        RoleProfileStatusResource resource = newRoleProfileStatusResource().build();

        setupGetWithRestResultExpectations(format(restUrl, userId) + "/" + role.name(), RoleProfileStatusResource.class, resource);

        assertEquals(resource, service.findByUserIdAndProfileRole(userId, role).getSuccess());
    }
}