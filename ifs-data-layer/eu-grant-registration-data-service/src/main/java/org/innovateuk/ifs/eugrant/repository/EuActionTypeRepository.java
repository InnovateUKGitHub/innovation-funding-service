package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.eugrant.domain.EuActionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EuActionTypeRepository extends CrudRepository<EuActionType, Long> {
    List<EuActionType> findAllByOrderByPriorityAsc();
}
