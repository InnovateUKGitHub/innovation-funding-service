package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.invite.repository.AccFinanceContactInviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccFinanceContactInviteServiceImpl extends AccInviteServiceImpl implements AccFinanceContactInviteService {

    @Autowired
    private AccFinanceContactInviteRepository accFinanceContactInviteRepository;

    @Override
    public InviteRepository getInviteRepository() {
        return accFinanceContactInviteRepository;
    }

    @Override
    public ProjectParticipantRole getProjectParticipantRole() {
        return ProjectParticipantRole.ACC_PROJECT_FINANCE_CONTACT;
    }
}
