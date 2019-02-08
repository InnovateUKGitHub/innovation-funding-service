package org.innovateuk.ifs.project.monitor.repository;

import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.monitor.domain.MonitoringOfficerInvite;

import java.util.List;

public interface MonitoringOfficerInviteRepository extends InviteRepository<MonitoringOfficerInvite> {

    List<StakeholderInvite> findByAndStatus(InviteStatus status);

    boolean existsByStatusAndEmail(InviteStatus sent, String email);
}