package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.monitoring.domain.ProjectMonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectMonitoringOfficerRepository extends PagingAndSortingRepository<ProjectMonitoringOfficer, Long> { // extends CompetitionParticipantRepository<Stakeholder> {

    boolean existsByUserEmailAndRole(String email, ProjectParticipantRole role);
}