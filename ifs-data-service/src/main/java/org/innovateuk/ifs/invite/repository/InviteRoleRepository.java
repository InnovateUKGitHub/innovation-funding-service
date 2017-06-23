package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InviteRoleRepository extends PagingAndSortingRepository<RoleInvite, Long> {

    List<RoleInvite> findByRoleId(Long roleId);

    List<RoleInvite> findByRoleIdAndEmail(Long roleId, String email);

    RoleInvite getByHash(String hash);
}

