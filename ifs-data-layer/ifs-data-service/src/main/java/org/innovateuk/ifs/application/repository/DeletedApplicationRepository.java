package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.DeletedApplicationAudit;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface DeletedApplicationRepository extends Repository<DeletedApplicationAudit, Long> {

    List<DeletedApplicationAudit> findByApplicationId(long applicationId);

    DeletedApplicationAudit save(DeletedApplicationAudit deletedApplicationAudit);
}
