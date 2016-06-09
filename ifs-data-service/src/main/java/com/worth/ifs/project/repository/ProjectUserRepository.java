package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.ProjectUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectUserRepository extends PagingAndSortingRepository<ProjectUser, Long> {

    List<ProjectUser> findByProjectId(Long projectId);
}
