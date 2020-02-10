package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleProfileStatusRepository extends CrudRepository<RoleProfileStatus, Long> {

    List<RoleProfileStatus> findByUserId(long userId);

    Optional<RoleProfileStatus> findByUserIdAndProfileRole(long userId, ProfileRole role);

    Page<RoleProfileStatus> findByRoleProfileStateAndProfileRoleAndUserEmailContainingAndUserStatus(
            RoleProfileState roleProfileState, ProfileRole role, String filter, UserStatus userStatus, Pageable pageable);
}