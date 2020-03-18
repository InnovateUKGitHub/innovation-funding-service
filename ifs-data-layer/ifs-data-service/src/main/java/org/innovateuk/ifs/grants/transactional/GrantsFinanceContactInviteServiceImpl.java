package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.grants.repository.GrantsFinanceContactInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantsFinanceContactInviteServiceImpl extends GrantsInviteServiceImpl implements GrantsFinanceContactInviteService {

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
}
