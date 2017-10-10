package org.innovateuk.ifs.setup.repository;

import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SetupStatusRepository extends PagingAndSortingRepository<SetupStatus, Long> {
    List<SetupStatus> findByTargetClassNameAndTargetId(String targetClassName, Long targetId);

    List<SetupStatus> findByClassNameAndParentId(String className, Long parentId);

    List<SetupStatus> findByTargetClassNameAndTargetIdAndParentId(String targetClassName, Long targetId, Long parentId);

    SetupStatus findByClassNameAndClassPk(String className, Long classPk);

    SetupStatus findByClassNameAndClassPkAndTargetClassNameAndTargetId(String className, Long classPk, String targetClassName, Long targetId);
}