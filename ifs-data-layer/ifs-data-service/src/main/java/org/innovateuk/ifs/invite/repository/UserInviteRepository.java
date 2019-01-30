package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.Invite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Base repository to extend to access {@link Invite} based queries.
 *
 * This does not include any queries against the target or owner entities.
 */
public interface UserInviteRepository extends CrudRepository<Invite, Long> {

    List<Invite> findByEmail(String email);
}
