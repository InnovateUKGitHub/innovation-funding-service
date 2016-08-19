package com.worth.ifs.project.repository;

import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.project.domain.ProjectUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectUserRepository extends PagingAndSortingRepository<ProjectUser, Long> {

    List<ProjectUser> findByProjectId(Long projectId);

    ProjectUser findOneByProjectIdAndUserIdAndOrganisationIdAndRole(long projectId, long userId, long organisationId, ProjectParticipantRole role);

    List<ProjectUser> findByProjectIdAndUserIdAndRole(long projectId, long userId, ProjectParticipantRole role);

    List<ProjectUser> findByUserId(long userId);

    ProjectUser findByProjectIdAndRoleAndUserId(long projectId, ProjectParticipantRole role, long userId);
}
