package org.innovateuk.ifs.setup.repository;

import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface SetupStatusRepository extends PagingAndSortingRepository<SetupStatus, Long> {
    List<SetupStatus> findByTargetClassNameAndTargetId(String targetClassName, Long targetId);

    List<SetupStatus> findByClassNameAndParentId(String className, Long parentId);

    List<SetupStatus> findByTargetClassNameAndTargetIdAndParentId(String targetClassName, Long targetId, Long parentId);

    Optional<SetupStatus> findByClassNameAndClassPk(String className, Long classPk);
}