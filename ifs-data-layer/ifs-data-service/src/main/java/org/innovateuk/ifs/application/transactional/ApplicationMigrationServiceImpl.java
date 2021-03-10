package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.application.repository.ApplicationMigrationRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationMigrationServiceImpl implements ApplicationMigrationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMigrationRepository applicationMigrationRepository;

    @Override
    public ServiceResult<Optional<ApplicationMigration>> findByApplicationIdAndStatus(long applicationId, MigrationStatus status) {
        return serviceSuccess(applicationMigrationRepository.findByApplicationIdAndStatus(applicationId, status));
    }

    @Override
    public ServiceResult<Application> migrateApplication(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(application -> {
                    Application migratedApplication = new Application(application);
                    return applicationRepository.save(migratedApplication);
                });
    }
}
