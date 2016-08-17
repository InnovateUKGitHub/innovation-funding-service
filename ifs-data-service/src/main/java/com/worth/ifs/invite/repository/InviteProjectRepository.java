package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.ProjectInvite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InviteProjectRepository extends PagingAndSortingRepository<ProjectInvite, Long> {

    List<ProjectInvite> findByProjectId(@Param("projectId") Long projectId);
    ProjectInvite getByHash(@Param("hash") String hash);
}
