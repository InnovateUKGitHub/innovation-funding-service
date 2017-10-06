package org.innovateuk.ifs.setup.repository;

import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface SetupStatusRepository extends PagingAndSortingRepository<SetupStatus, Long> {
    List<SetupStatus> findByTargetIdAndTargetClassName(Long targetId, String targetClassName);

    List<SetupStatus> findByTargetIdAndTargetClassNameAndParentId(Long targetId, String targetClassName, Long parentId);

    Optional<SetupStatus> findByClassPkAndClassName(Long classPk, String className);
}