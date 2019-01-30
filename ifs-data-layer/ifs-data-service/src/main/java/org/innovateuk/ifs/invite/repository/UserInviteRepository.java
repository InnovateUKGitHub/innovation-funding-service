package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.Invite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserInviteRepository extends CrudRepository<Invite, Long> {

    List<Invite> findByEmail(String email);
}
