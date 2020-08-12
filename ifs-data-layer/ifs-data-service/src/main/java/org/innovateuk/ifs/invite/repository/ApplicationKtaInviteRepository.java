package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;

import java.util.List;
import java.util.Optional;

public interface ApplicationKtaInviteRepository extends InviteRepository<ApplicationKtaInvite> {

    Optional<ApplicationKtaInvite> findByApplicationId(Long applicationId);
}
