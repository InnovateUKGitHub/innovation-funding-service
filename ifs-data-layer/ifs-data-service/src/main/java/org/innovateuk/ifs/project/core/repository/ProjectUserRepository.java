package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectUserRepository extends PagingAndSortingRepository<ProjectUser, Long> {

    List<ProjectUser> findByProjectId(Long projectId);

    List<ProjectUser> findByProjectIdAndRoleIsIn(Long projectId, List<ProjectParticipantRole> role);

    List<ProjectUser> findByProjectIdAndUserIdAndRoleIsIn(long projectId, long userId, List<ProjectParticipantRole> role);

    ProjectUser findOneByProjectIdAndUserIdAndRoleIsIn(long projectId, long userId, List<ProjectParticipantRole> role);

    List<ProjectUser> findByProjectIdAndOrganisationId(long projectId, long organisationId);

    ProjectUser findOneByProjectIdAndUserIdAndOrganisationIdAndRole(long projectId, long userId, long organisationId, ProjectParticipantRole role);

    List<ProjectUser> findByProjectIdAndUserIdAndRole(long projectId, long userId, ProjectParticipantRole role);

    List<ProjectUser> findByUserId(long userId);

    List<ProjectUser> findByProjectIdAndUserId(long projectId, long userId);

    List<ProjectUser> findByUserIdAndRole(long userId, ProjectParticipantRole role);

    ProjectUser findByProjectIdAndRoleAndUserId(long projectId, ProjectParticipantRole role, long userId);

    Optional<ProjectUser> findByProjectIdAndRole(long projectId, ProjectParticipantRole role);

    void deleteAllByProjectIdAndOrganisationId(long projectId, long organisationId);

    boolean existsByProjectApplicationCompetitionIdAndUserId(long competitionId, long userId);
}
