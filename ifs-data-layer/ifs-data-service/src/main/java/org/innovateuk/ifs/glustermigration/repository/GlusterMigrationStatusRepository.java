package org.innovateuk.ifs.glustermigration.repository;

import org.innovateuk.ifs.glustermigration.domain.GlusterMigrationStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 */
public interface GlusterMigrationStatusRepository extends CrudRepository<GlusterMigrationStatus, String> {

    List<GlusterMigrationStatus> findGlusterMigrationStatusByStatusEquals(String status);

}
