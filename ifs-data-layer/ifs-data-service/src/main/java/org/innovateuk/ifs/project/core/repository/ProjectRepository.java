package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>{
    @Override
    List<Project> findAll();
    Project findOneByApplicationId(final Long applicationId);
    List<Project> findByApplicationCompetitionId(final Long competitionId);
    List<Project> findByApplicationCompetitionIdAndProjectProcessActivityStateNotIn(final Long competitionId, Collection<ProjectState> projectStates);
}
