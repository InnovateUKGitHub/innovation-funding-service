package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusBuilder.newRoleProfileStatus;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class RoleProfileStatusServiceImplTest extends BaseServiceUnitTest<RoleProfileStatusServiceImpl> {

    @InjectMocks
    private RoleProfileStatusService roleProfileStatusService = new RoleProfileStatusServiceImpl();

    @Mock
    private RoleProfileStatusRepository roleProfileStatusRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private RoleProfileStatusMapper roleProfileStatusMapper;

    @Mock
    private UserMapper userMapperMock;

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

        when(roleProfileStatusRepositoryMock.findByUserId(userId)).thenReturn(roleProfileStatus);
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

        when(roleProfileStatusRepositoryMock.findByUserIdAndProfileRole(userId, profileRole)).thenReturn(Optional.of(roleProfileStatus));
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

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        when(roleProfileStatusMapper.mapToResource(roleProfileStatus)).thenReturn(roleProfileStatusResource);
        when(roleProfileStatusRepositoryMock.findByUserIdAndProfileRole(userId, ProfileRole.ASSESSOR)).thenReturn(Optional.of(roleProfileStatus));

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

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        when(roleProfileStatusMapper.mapToResource(roleProfileStatus)).thenReturn(roleProfileStatusResource);
        when(roleProfileStatusRepositoryMock.findByUserIdAndProfileRole(userId, ProfileRole.ASSESSOR)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.updateUserStatus(userId, roleProfileStatusResource);

        assertTrue(result.isSuccess());
    }


    @Test
    public void findByRoleProfile() {
        RoleProfileState roleProfileState = RoleProfileState.ACTIVE;
        ProfileRole profileRole = ProfileRole.ASSESSOR;
        String filter = "";
        Pageable pageable = PageRequest.of(0, 5);
        int numberOfUsers = 2;

        User[] expectedUsers = newUser().buildArray(numberOfUsers, User.class);
        UserResource[] expectedUserResources = newUserResource().buildArray(expectedUsers.length, UserResource.class);
        List<RoleProfileStatus> expectedProfileStatuses = newRoleProfileStatus().withUser(expectedUsers).build(expectedUsers.length);
        Page<RoleProfileStatus> expectedPage = new PageImpl<>(expectedProfileStatuses, pageable, expectedUsers.length);

        when(roleProfileStatusRepositoryMock.findByRoleProfileStateAndProfileRoleAndUserEmailContainingAndUserStatus(
                roleProfileState, profileRole, filter, UserStatus.ACTIVE, pageable)
        )
                .thenReturn(expectedPage);

        for (int i = 0; i < expectedUsers.length; i++) {
            when(userMapperMock.mapToResource(expectedUsers[i])).thenReturn(expectedUserResources[i]);
        }
        UserPageResource userPageResource = service.findByRoleProfile(roleProfileState, profileRole, filter, pageable).getSuccess();

        assertEquals(asList(expectedUserResources), userPageResource.getContent());
        assertEquals(numberOfUsers, userPageResource.getTotalElements());
        assertEquals(pageable.getPageNumber(), userPageResource.getNumber());
        assertEquals(pageable.getPageSize(), userPageResource.getSize());

        InOrder inOrder = inOrder(roleProfileStatusRepositoryMock, userMapperMock);
        inOrder.verify(roleProfileStatusRepositoryMock).findByRoleProfileStateAndProfileRoleAndUserEmailContainingAndUserStatus(
                roleProfileState, profileRole, filter, UserStatus.ACTIVE, pageable);
        stream(expectedUsers).forEachOrdered(u -> inOrder.verify(userMapperMock).mapToResource(u));
        inOrder.verifyNoMoreInteractions();
    }
}