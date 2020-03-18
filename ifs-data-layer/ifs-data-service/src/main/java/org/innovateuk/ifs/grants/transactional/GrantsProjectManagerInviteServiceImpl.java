package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.grants.repository.GrantsProjectManagerInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GrantsProjectManagerInviteServiceImpl extends GrantsInviteServiceImpl implements GrantsProjectManagerInviteService {

    @Autowired
    private GrantsProjectManagerInviteRepository grantsProjectManagerInviteRepository;

    @Override
    public InviteRepository getInviteRepository() {
        return grantsProjectManagerInviteRepository;
    }

    @Override
    public ProjectParticipantRole getProjectParticipantRole() {
        return ProjectParticipantRole.ACC_PROJECT_MANAGER;
    }
}
