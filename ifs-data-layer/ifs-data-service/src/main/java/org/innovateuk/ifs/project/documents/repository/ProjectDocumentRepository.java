package org.innovateuk.ifs.project.documents.repository;

import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectDocumentRepository extends PagingAndSortingRepository<ProjectDocument, Long> {
}