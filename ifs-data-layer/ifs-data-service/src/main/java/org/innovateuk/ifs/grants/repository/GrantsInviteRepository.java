package org.innovateuk.ifs.grants.repository;

import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.invite.repository.InviteRepository;

import java.util.List;

public interface GrantsInviteRepository extends InviteRepository<GrantsInvite> {

    List<GrantsInvite> findByProjectId(long projectId);

    boolean existsByProjectIdAndEmail(long projectId, String email);
}
