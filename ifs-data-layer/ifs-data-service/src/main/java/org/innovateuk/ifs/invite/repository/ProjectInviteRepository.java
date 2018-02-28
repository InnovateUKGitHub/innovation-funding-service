package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ProjectInvite;

import java.util.List;
import java.util.Set;

public interface ProjectInviteRepository extends InviteRepository<ProjectInvite> {

    List<ProjectInvite> findByOrganisationNameLikeAndStatusIn(String organisationName, Set<InviteStatus> status);

    List<ProjectInvite> findByProjectId(long projectId);

    List<ProjectInvite> findByProjectIdAndEmail(long projectId, String email);
}
