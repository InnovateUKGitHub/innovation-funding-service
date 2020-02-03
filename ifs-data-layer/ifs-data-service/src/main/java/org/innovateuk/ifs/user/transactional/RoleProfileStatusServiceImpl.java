package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


@Service
public class RoleProfileStatusServiceImpl implements RoleProfileStatusService {

    @Autowired
    private RoleProfileStatusRepository roleProfileStatusRepository;

    @Autowired
    private RoleProfileStatusMapper roleProfileStatusMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ServiceResult<Void> updateUserStatus(long userId, RoleProfileStatusResource roleProfileStatusResource) {
        return getUser(userId).andOnSuccessReturnVoid(user -> updateRoleProfileStatus(user, roleProfileStatusResource));
    }

    @Override
    public ServiceResult<List<RoleProfileStatusResource>> findByUserId(long userId) {
        return find(roleProfileStatusRepository.findByUserId(userId), notFoundError(RoleProfileStatus.class, userId))
                .andOnSuccessReturn(rp -> rp.stream()
                        .map(roleProfileStatusMapper::mapToResource)
                        .collect(toList()));
    }

    @Override
    public ServiceResult<RoleProfileStatusResource> findByUserIdAndProfileRole(long userId, ProfileRole profileRole) {
        return find(roleProfileStatusRepository.findByUserIdAndProfileRole(userId, profileRole), notFoundError(RoleProfileStatus.class, userId))
                .andOnSuccessReturn(roleProfileStatusMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<UserPageResource> findByRoleProfile(RoleProfileState state, ProfileRole profileRole, String filter, Pageable pageable) {
        return userPageResource(
                roleProfileStatusRepository.findByRoleProfileStateAndProfileRoleAndUserEmailContaining(state, profileRole, filter, pageable)
                        .map(RoleProfileStatus::getUser)
        );
    }

    private ServiceResult<UserPageResource> userPageResource(Page<User> pagedResult) {
        return serviceSuccess(new UserPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getContent().stream().map(userMapper::mapToResource).collect(toList()),
                pagedResult.getNumber(),
                pagedResult.getSize())
        );
    }

    private void updateRoleProfileStatus(User user, RoleProfileStatusResource roleProfileStatusResource) {
        findOrCreateRoleProfileStatus(user, roleProfileStatusResource).andOnSuccessReturnVoid(roleProfileStatus -> {
            roleProfileStatus.setRoleProfileState(roleProfileStatusResource.getRoleProfileState());
            roleProfileStatus.setDescription(roleProfileStatusResource.getDescription());
            roleProfileStatusRepository.save(roleProfileStatus);
        });
    }

    private ServiceResult<RoleProfileStatus> findOrCreateRoleProfileStatus(User user, RoleProfileStatusResource roleProfileStatusResource) {

        Optional<RoleProfileStatus> roleProfileStatus =
                roleProfileStatusRepository.findByUserIdAndProfileRole(roleProfileStatusResource.getUserId(), roleProfileStatusResource.getProfileRole());

        if (roleProfileStatus.isPresent()) {
            return serviceSuccess(roleProfileStatus.get());
        }

        return serviceSuccess(new RoleProfileStatus(user, roleProfileStatusResource.getProfileRole()));
    }

    private ServiceResult<User> getUser(long id) {
        return find(userRepository.findById(id), notFoundError(User.class, id));
    }
}
