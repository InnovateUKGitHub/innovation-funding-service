package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.grants.repository.GrantsFinanceContactInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.grants.transactional.GrantsInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION;

@Service
public class GrantsFinanceContactInviteServiceImpl extends GrantsInviteServiceImpl implements GrantsFinanceContactInviteService {

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private GrantsFinanceContactInviteRepository grantsFinanceContactInviteRepository;

    @Override
    public InviteRepository getInviteRepository() {
        return grantsFinanceContactInviteRepository;
    }

    @Override
    public ProjectParticipantRole getProjectParticipantRole() {
        return ProjectParticipantRole.ACC_PROJECT_FINANCE_CONTACT;
    }

    @Override
    public Notification getNotification(GrantsInvite grantsInvite) {

        Map<String, Object> notificationArguments = new HashMap<>();
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(grantsInvite.getName(), grantsInvite.getEmail());

        return new Notification(from, singletonList(to), INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);
    }
}
