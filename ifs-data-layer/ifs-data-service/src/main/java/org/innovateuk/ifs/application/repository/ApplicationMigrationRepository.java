package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationMigrationRepository  extends CrudRepository<ApplicationMigration, Long>  {

    Optional<ApplicationMigration> findByApplicationIdAndStatus(Long applicationId, MigrationStatus status);
}
