package org.innovateuk.ifs.euactiontype.repository;

import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EuActionTypeRepository extends CrudRepository<EuActionType, Long> {
    List<EuActionType> findAllByOrderByPriorityAsc();
}
