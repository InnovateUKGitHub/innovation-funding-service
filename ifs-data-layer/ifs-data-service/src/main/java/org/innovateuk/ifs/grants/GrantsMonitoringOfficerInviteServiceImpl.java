package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Transactional and secured service implementation providing operations around inviting monitoring officers.
 */
@Service
public class GrantsMonitoringOfficerInviteServiceImpl extends GrantsInviteServiceImpl implements GrantsMonitoringOfficerInviteService {

    @Autowired
    private GrantsMonitoringOfficerInviteRepository grantsMonitoringOfficerInviteRepository;

    @Override
    public InviteRepository getInviteRepository() {
        return grantsMonitoringOfficerInviteRepository;
    }

    @Override
    public ProjectParticipantRole getProjectParticipantRole() {
        return ProjectParticipantRole.ACC_MONITORING_OFFICER;
    }
}
