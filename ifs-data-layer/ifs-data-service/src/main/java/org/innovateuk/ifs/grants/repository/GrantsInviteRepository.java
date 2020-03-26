package org.innovateuk.ifs.grants.repository;

import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.invite.repository.InviteRepository;

public interface GrantsInviteRepository<Invite extends GrantsInvite> extends InviteRepository<Invite> {

    GrantsInvite save(Invite invite);
}
