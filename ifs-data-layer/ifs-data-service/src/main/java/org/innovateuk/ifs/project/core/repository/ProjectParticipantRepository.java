package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.ProjectParticipant;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectParticipantRepository extends PagingAndSortingRepository<ProjectParticipant, Long> {
    List<ProjectParticipant> findByProjectId(Long projectId);
}
