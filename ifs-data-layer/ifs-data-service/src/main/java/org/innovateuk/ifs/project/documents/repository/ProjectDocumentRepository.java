package org.innovateuk.ifs.project.documents.repository;

import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectDocumentRepository extends PagingAndSortingRepository<ProjectDocument, Long> {

    List<ProjectDocument> findAllByProjectId(Long projectId);
}