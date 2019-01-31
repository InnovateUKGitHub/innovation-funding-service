package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;

import java.util.List;
import java.util.Set;

public interface ProjectUserInviteRepository extends InviteRepository<ProjectUserInvite> {

    List<ProjectUserInvite> findByOrganisationNameLikeAndStatusIn(String organisationName, Set<InviteStatus> status);

    List<ProjectUserInvite> findByProjectId(long projectId);

    List<ProjectUserInvite> findByProjectIdAndEmail(long projectId, String email);
}