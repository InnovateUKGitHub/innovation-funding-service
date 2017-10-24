package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface InviteProjectRepository extends PagingAndSortingRepository<ProjectInvite, Long> {

    List<ProjectInvite> findByStatusIn(Set<InviteStatus> status);

    List<ProjectInvite> findByProjectId(Long projectId);

    List<ProjectInvite> findByProjectIdAndEmail(Long projectId, String email);

    ProjectInvite getByHash(String hash);
}
