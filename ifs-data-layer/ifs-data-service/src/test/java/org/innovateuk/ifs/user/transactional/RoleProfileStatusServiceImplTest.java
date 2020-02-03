package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.user.builder.RoleProfileStatusBuilder.newRoleProfileStatus;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class RoleProfileStatusServiceImplTest extends BaseServiceUnitTest<RoleProfileStatusServiceImpl> {

    @InjectMocks
    private RoleProfileStatusService roleProfileStatusService = new RoleProfileStatusServiceImpl();

    @Mock
    private RoleProfileStatusRepository roleProfileStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    RoleProfileStatusMapper roleProfileStatusMapper;

    @Override
    protected RoleProfileStatusServiceImpl supplyServiceUnderTest() {
        return new RoleProfileStatusServiceImpl();
    }

    @Test
    public void findByUserId() {
        long userId = 1l;
        User user = newUser().withId(userId).build();

        List<RoleProfileStatus> roleProfileStatus = newRoleProfileStatus().withUser(user).build(1);
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withUserId(userId).build();

        when(roleProfileStatusRepository.findByUserId(userId)).thenReturn(roleProfileStatus);
        when(roleProfileStatusMapper.mapToResource(roleProfileStatus.get(0))).thenReturn(roleProfileStatusResource);

        ServiceResult<List<RoleProfileStatusResource>> result = service.findByUserId(userId);

        assertTrue(result.isSuccess());
        assertEquals(roleProfileStatusResource, result.getSuccess().get(0));
    }

    @Test
    public void findByUserIdAndProfileRole() {
        long userId = 1l;
        ProfileRole profileRole = ProfileRole.ASSESSOR;
        User user = newUser().withId(userId).build();

        RoleProfileStatus roleProfileStatus = newRoleProfileStatus().withUser(user).build();
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withUserId(userId).build();

        when(roleProfileStatusRepository.findByUserIdAndProfileRole(userId, profileRole)).thenReturn(Optional.of(roleProfileStatus));
        when(roleProfileStatusMapper.mapToResource(roleProfileStatus)).thenReturn(roleProfileStatusResource);

        ServiceResult<RoleProfileStatusResource> result = service.findByUserIdAndProfileRole(userId, profileRole);

        assertTrue(result.isSuccess());
        assertEquals(roleProfileStatusResource, result.getSuccess());
    }

    @Test
    public void updateUserStatus() {
        long userId = 1l;
        User user = newUser().withId(userId).build();

        RoleProfileStatus roleProfileStatus = newRoleProfileStatus().withUser(user).build();
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource()
                .withUserId(userId)
                .withRoleProfileState(RoleProfileState.UNAVAILABLE)
                .withProfileRole(ProfileRole.ASSESSOR)
                .withDescription("Description")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleProfileStatusMapper.mapToResource(roleProfileStatus)).thenReturn(roleProfileStatusResource);
        when(roleProfileStatusRepository.findByUserIdAndProfileRole(userId, ProfileRole.ASSESSOR)).thenReturn(Optional.of(roleProfileStatus));

        ServiceResult<Void> result = service.updateUserStatus(userId, roleProfileStatusResource);

        assertTrue(result.isSuccess());
    }


    @Test
    public void updateUserStatusCreate() {
        long userId = 1l;
        User user = newUser().withId(userId).build();

        RoleProfileStatus roleProfileStatus = newRoleProfileStatus().withUser(user).build();
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource()
                .withUserId(userId)
                .withRoleProfileState(RoleProfileState.UNAVAILABLE)
                .withProfileRole(ProfileRole.ASSESSOR)
                .withDescription("Description")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleProfileStatusMapper.mapToResource(roleProfileStatus)).thenReturn(roleProfileStatusResource);
        when(roleProfileStatusRepository.findByUserIdAndProfileRole(userId, ProfileRole.ASSESSOR)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.updateUserStatus(userId, roleProfileStatusResource);

        assertTrue(result.isSuccess());
    }
}
