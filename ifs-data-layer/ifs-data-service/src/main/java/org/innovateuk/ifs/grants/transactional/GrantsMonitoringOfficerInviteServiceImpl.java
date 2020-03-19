package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.grants.repository.GrantsMonitoringOfficerInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.grants.transactional.GrantsInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION;

/**
 * Transactional and secured service implementation providing operations around inviting monitoring officers.
 */
@Service
public class GrantsMonitoringOfficerInviteServiceImpl extends GrantsInviteServiceImpl implements GrantsMonitoringOfficerInviteService {

    @Autowired
    private SystemNotificationSource systemNotificationSource;

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

    @Override
    public Notification getNotification(GrantsInvite grantsInvite) {

        Map<String, Object> notificationArguments = new HashMap<>();
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(grantsInvite.getName(), grantsInvite.getEmail());

        return new Notification(from, singletonList(to), INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);
    }
}
