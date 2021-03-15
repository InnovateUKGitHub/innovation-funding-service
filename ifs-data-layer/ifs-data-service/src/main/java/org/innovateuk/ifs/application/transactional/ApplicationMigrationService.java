package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface ApplicationMigrationService {

    @SecuredBySpring(value = "FIND_APPLICATION_FOR_MIGRATION", description = "A System Maintenance User can find applications to migrate.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Optional<ApplicationMigration>> findByApplicationIdAndStatus(long applicationId, MigrationStatus status);

    @SecuredBySpring(value = "MIGRATE_APPLICATION", description = "A System Maintenance User can migrate application.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Void> migrateApplication(long applicationId);

    @SecuredBySpring(value = "UPDATE_APPLICATION_MIGRATION_STATUS", description = "A System Maintenance User can update application migration status.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<ApplicationMigration> updateApplicationMigrationStatus(ApplicationMigration applicationMigration);
}
