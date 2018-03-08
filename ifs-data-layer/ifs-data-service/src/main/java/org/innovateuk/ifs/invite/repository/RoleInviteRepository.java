package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.RoleInvite;

import java.util.List;
import java.util.Optional;

public interface RoleInviteRepository extends InviteRepository<RoleInvite> {

    List<RoleInvite> findByRoleId(long roleId);

    Optional<RoleInvite> findOneByRoleIdAndEmail(long roleId, String email);
}

