package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.Project;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>{
    Project findById(Long id);

    @Override
    List<Project> findAll();
}
