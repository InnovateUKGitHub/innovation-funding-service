package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.eugrant.domain.EuActionType;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface EuActionTypeRepository extends Repository<EuActionType, Long> {
    List<EuActionType> findAllByOrderByPriorityAsc();
}
