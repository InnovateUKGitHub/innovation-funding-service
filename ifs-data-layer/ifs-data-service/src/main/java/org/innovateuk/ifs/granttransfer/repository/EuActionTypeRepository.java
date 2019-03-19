package org.innovateuk.ifs.granttransfer.repository;

import org.innovateuk.ifs.granttransfer.domain.EuActionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EuActionTypeRepository extends CrudRepository<EuActionType, Long> {

    List<EuActionType> findAllByOrderByPriorityAsc();

    Optional<EuActionType> findOneByNameIgnoreCase(String name);
}
