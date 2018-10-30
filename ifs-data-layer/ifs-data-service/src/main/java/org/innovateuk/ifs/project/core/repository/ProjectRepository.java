package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>{

    String PROJECTS_BY_APP_ID_LIKE_AND_COMP_ID_AND_NOT_IN_STATE = "SELECT DISTINCT pp.target FROM ProjectProcess pp " +
            " WHERE pp.target.application.competition.id = :compId " +
            " AND pp.activityState NOT IN :states " +
            " AND (str(pp.target.application.id) LIKE CONCAT('%', :applicationSearchString, '%'))";

    String PROJECTS_READY_TO_SEND = "SELECT p FROM Project p " +
            " WHERE p.offerSubmittedDate is NOT NULL";

    @Override
    List<Project> findAll();
    Project findOneByApplicationId(final Long applicationId);
    List<Project> findByApplicationCompetitionId(final Long competitionId);

    @Query(PROJECTS_BY_APP_ID_LIKE_AND_COMP_ID_AND_NOT_IN_STATE)
    List<Project> searchByCompetitionIdAndApplicationIdLikeAndProjectStateNotIn(@Param("compId") long competitionId, @Param("applicationSearchString") String applicationSearchString, @Param("states") Collection<ProjectState> projectStates);

    @Query(PROJECTS_READY_TO_SEND)
    List<Project> findReadyToSend();
}
