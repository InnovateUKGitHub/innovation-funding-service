package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;

import java.util.List;

public interface ApplicationKtaInviteRepository extends InviteRepository<ApplicationKtaInvite> {

    List<ApplicationKtaInvite> findByApplicationId(Long applicationId);
}
