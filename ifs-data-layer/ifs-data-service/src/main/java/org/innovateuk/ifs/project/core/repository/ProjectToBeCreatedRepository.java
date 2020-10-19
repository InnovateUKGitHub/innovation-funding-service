package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProjectToBeCreatedRepository extends PagingAndSortingRepository<ProjectToBeCreated, Long> {

    Page<ProjectToBeCreated> findByPendingIsTrue(Pageable pageable);

    Optional<ProjectToBeCreated> findByApplicationId(long applicationId);

}
