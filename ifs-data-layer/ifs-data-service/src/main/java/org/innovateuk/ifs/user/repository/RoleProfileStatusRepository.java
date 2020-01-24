package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleProfileStatusRepository extends CrudRepository<RoleProfileStatus, Long> {

    Optional<RoleProfileStatus> findByUserId(long userId);
}
