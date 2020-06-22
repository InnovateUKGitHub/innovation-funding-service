package org.innovateuk.ifs.project.grants.populator;

import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grants.viewmodel.ManageInvitationsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManageInvitationsModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    public ManageInvitationsViewModel populateManageInvitationsViewModel(long projectId) {

        ProjectResource project = projectService.getById(projectId);
        List<SentGrantsInviteResource> grants = grantsInviteRestService.getAllForProject(projectId).getSuccess()
                .stream().filter(grant -> InviteStatus.SENT == grant.getStatus())
                .collect(Collectors.toList());

        return new ManageInvitationsViewModel(project.getCompetition(),
                                            project.getCompetitionName(),
                                            project.getId(),
                                            project.getName(),
                                            project.getApplication(),
                                            grants);
    }
}
