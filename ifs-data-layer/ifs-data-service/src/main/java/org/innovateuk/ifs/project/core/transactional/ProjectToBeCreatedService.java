package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.Optional;

public interface ProjectToBeCreatedService {
    Optional<Long> findProjectToCreate();

    ServiceResult<Void> createProject(long applicationId);

    ServiceResult<Void> markApplicationReadyToBeCreated(long applicationId, String emailBody);
}
