package org.innovateuk.ifs.project.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;

import java.util.List;

public interface ProjectPartnerInviteRepository extends InviteRepository<ProjectPartnerInvite> {
    List<ProjectPartnerInvite> findByProjectIdAndEmail(long projectId, String email);
    List<ProjectPartnerInvite> findByProjectId(long projectId);
    void deleteByProjectIdAndInviteOrganisationOrganisationId(long projectId, long organisationId);
    boolean existsByProjectIdAndStatus(long projectId, InviteStatus inviteStatus);
}