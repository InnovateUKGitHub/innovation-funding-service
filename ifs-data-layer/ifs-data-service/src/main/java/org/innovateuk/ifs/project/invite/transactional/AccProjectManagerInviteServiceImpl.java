package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.invite.repository.AccProjectManagerInviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AccProjectManagerInviteServiceImpl extends AccInviteServiceImpl implements AccProjectManagerInviteService {

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
