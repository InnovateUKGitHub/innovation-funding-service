package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InviteRoleRepository extends PagingAndSortingRepository<RoleInvite, Long> {

    List<RoleInvite> findByRoleId(Long roleId);

    List<RoleInvite> findByEmail(String email);

    List<RoleInvite> findByRoleIdAndEmail(Long roleId, String email);

    RoleInvite getByHash(String hash);
}

