package org.innovateuk.ifs.granttransfer.repository;

import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.springframework.data.repository.CrudRepository;

public interface EuGrantTransferRepository extends CrudRepository<EuGrantTransfer, Long> {

    EuGrantTransfer findByApplicationId(long applicationId);
}
