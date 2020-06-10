package org.innovateuk.ifs.project.grants.populator;

import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.project.grants.viewmodel.ManageInvitationsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageInvitationsModelPopulator {

    public ManageInvitationsViewModel populateManageInvitationsViewModel(ProjectResource project,
                                                                         List<SentGrantsInviteResource> grants) {

        return new ManageInvitationsViewModel(project.getCompetition(),
                                            project.getCompetitionName(),
                                            project.getId(),
                                            project.getName(),
                                            project.getApplication(),
                                            grants);
    }
}
