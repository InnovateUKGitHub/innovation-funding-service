package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.config.repository.RefreshableCrudRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ProjectRepository extends RefreshableCrudRepository<Project, Long> {

    String PROJECTS_BY_APP_ID_LIKE_AND_COMP_ID = "SELECT DISTINCT pp.target FROM ProjectProcess pp " +
            " WHERE pp.target.application.competition.id = :competitionId " +
            " AND (str(pp.target.application.id) LIKE CONCAT('%', :applicationSearchString, '%'))";

    @Override
    List<Project> findAll();
    Project findOneByApplicationId(long applicationId);
    List<Project> findByApplicationCompetitionId(long competitionId);

    int countByApplicationCompetitionId(long competitionId);
    int countByApplicationCompetitionIdAndProjectProcessActivityStateIn(long competitionId, Set<ProjectState> states);

    List<Project> findByApplicationCompetitionIdAndProjectProcessActivityStateIn(long competitionId, Set<ProjectState> states);

    @Query(PROJECTS_BY_APP_ID_LIKE_AND_COMP_ID)
    List<Project> searchByCompetitionIdAndApplicationIdLike(long competitionId, String applicationSearchString);
}