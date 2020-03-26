package org.innovateuk.ifs.grants.repository;

import org.innovateuk.ifs.grants.domain.GrantsMonitoringOfficerInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;

public interface GrantsMonitoringOfficerInviteRepository extends GrantsInviteRepository<GrantsMonitoringOfficerInvite> {

    boolean existsByStatusAndEmail(InviteStatus status, String email);
}
