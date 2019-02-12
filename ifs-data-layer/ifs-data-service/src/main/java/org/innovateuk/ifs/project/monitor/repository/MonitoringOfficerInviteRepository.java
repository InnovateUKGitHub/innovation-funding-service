package org.innovateuk.ifs.project.monitor.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.monitor.domain.MonitoringOfficerInvite;

public interface MonitoringOfficerInviteRepository extends InviteRepository<MonitoringOfficerInvite> {

    boolean existsByStatusAndEmail(InviteStatus sent, String email);
}