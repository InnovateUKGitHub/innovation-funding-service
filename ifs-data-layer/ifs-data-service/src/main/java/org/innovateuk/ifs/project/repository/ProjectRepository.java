package org.innovateuk.ifs.project.repository;

import org.innovateuk.ifs.project.domain.Project;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>{
    @Override
    List<Project> findAll();
    Project findOneByApplicationId(final Long applicationId);
    List<Project> findByApplicationCompetitionId(final Long competitionId);
}
