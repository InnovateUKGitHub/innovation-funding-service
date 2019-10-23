package org.innovateuk.ifs.project.invite.repository;

import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;

import java.util.List;

public interface ProjectPartnerInviteRepository extends InviteRepository<ProjectPartnerInvite> {
    List<ProjectPartnerInvite> findByProjectIdAndEmail(long projectId, String email);
}