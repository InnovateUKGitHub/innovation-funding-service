package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;

public interface MonitoringOfficerInviteRepository extends InviteRepository<MonitoringOfficerInvite> {

    boolean existsByStatusAndEmail(InviteStatus status, String email);

    boolean existsByHash(String hash);

    default boolean sentInviteExistsByEmail(String email) {
        return existsByStatusAndEmail(InviteStatus.SENT, email);
    }
}