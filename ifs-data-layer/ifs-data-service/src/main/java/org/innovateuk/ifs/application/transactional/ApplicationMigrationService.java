package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ApplicationMigrationService {

    @SecuredBySpring(value = "FIND_APPLICATION_MIGRATION", description = "A System Maintenance User can find applications to migrate.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Optional<ApplicationMigration>> findApplicationByIdAndStatus(long applicationId, MigrationStatus status);

    @SecuredBySpring(value = "MIGRATE_APPLICATION", description = "A System Maintenance User can migrate application.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Application> migrateApplication(long applicationId);
}
