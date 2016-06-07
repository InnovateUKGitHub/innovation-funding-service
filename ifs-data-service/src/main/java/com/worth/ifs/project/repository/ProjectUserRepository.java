package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.ProjectUser;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectUserRepository extends PagingAndSortingRepository<ProjectUser, Long> {

}
