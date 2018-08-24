package org.innovateuk.ifs.competitionsetup.repository;

import org.innovateuk.ifs.competitionsetup.domain.ProjectDocument;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectDocumentRepository extends CrudRepository<ProjectDocument, Long> {

    List<ProjectDocument> findByCompetitionId(Long competitionId);
}

