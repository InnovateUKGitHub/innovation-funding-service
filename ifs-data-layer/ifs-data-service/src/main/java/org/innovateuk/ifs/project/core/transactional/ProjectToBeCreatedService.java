package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;

import java.util.Optional;

public interface ProjectToBeCreatedService {
    Optional<Long> findProjectToCreate(int index);

    ServiceResult<ScheduleResponse> createProject(long applicationId);

    ServiceResult<Void> markApplicationReadyToBeCreated(long applicationId, String emailBody);
}
