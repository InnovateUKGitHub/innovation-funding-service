package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;

public interface GrantsMonitoringOfficerInviteRepository extends InviteRepository<GrantsMonitoringOfficerInvite> {

    boolean existsByStatusAndEmail(InviteStatus status, String email);

    default boolean sentInviteExistsByEmail(String email) {
        return existsByStatusAndEmail(InviteStatus.SENT, email);
    }
}
