package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GrantsProjectManagerInviteServiceImpl extends GrantsInviteServiceImpl implements GrantsProjectManagerInviteService {

    @Autowired
    private AccProjectManagerInviteRepository accProjectManagerInviteRepository;

    @Override
    public InviteRepository getInviteRepository() {
        return accProjectManagerInviteRepository;
    }

    @Override
    public ProjectParticipantRole getProjectParticipantRole() {
        return ProjectParticipantRole.ACC_PROJECT_MANAGER;
    }
}
