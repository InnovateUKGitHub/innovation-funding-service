package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.DeletedApplicationAudit;
import org.springframework.data.repository.Repository;

public interface DeletedApplicationRepository extends Repository<DeletedApplicationAudit, Long> {

    DeletedApplicationAudit save(DeletedApplicationAudit deletedApplicationAudit);
}
