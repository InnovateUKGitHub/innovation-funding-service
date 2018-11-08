package org.innovateuk.ifs.euactiontype.repository;

import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EuActionTypeRepository extends CrudRepository<EuActionType, Long> {

    List<EuActionType> findAllByOrderByPriorityAsc();

    Optional<EuActionType> findOneByName(String name);
}
