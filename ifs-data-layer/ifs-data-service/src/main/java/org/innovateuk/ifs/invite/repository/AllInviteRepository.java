package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.Invite;

/**
 * Base repository to extend to access {@link Invite} based queries.
 *
 * This does not include any queries against the target or owner entities.
 */
public interface AllInviteRepository extends InviteRepository<Invite> {
}
