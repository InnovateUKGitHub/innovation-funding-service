package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InviteProjectRepository extends PagingAndSortingRepository<ProjectInvite, Long> {

    List<ProjectInvite> findByProjectId(Long projectId);

    List<ProjectInvite> findByProjectIdAndEmail(Long projectId, String email);

    ProjectInvite getByHash(String hash);
}
