package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.ProjectUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectUserRepository extends PagingAndSortingRepository<ProjectUser, Long> {

    List<ProjectUser> findByProjectId(Long projectId);

    ProjectUser findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(long projectId, long userId, long organisationId, long roleId);

    List<ProjectUser> findByProjectIdAndUserIdAndRoleId(long projectId, long userId, long roleId);

    List<ProjectUser> findByUserId(long userId);

    ProjectUser findByProjectIdAndRoleIdAndUserId(long projectId, long roleId, long userId);
}
