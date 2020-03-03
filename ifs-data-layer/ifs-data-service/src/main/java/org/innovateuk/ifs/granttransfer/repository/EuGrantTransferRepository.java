package org.innovateuk.ifs.granttransfer.repository;

import org.innovateuk.ifs.config.repository.RefreshableCrudRepository;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;

public interface EuGrantTransferRepository extends RefreshableCrudRepository<EuGrantTransfer, Long> {

    EuGrantTransfer findByApplicationId(long applicationId);

}
