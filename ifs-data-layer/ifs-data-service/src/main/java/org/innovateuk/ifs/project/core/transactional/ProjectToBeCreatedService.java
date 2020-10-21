package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface ProjectToBeCreatedService {
    @SecuredBySpring(value = "CREATE_PROJECT_SCHEDULE", description = "A System Maintinance User can create projects on schedule.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    Optional<Long> findProjectToCreate(int index);

    @SecuredBySpring(value = "CREATE_PROJECT_SCHEDULE", description = "A System Maintinance User can create projects on schedule.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<ScheduleResponse> createProject(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "MARK_APPLICATION_AS_READY_PS", description = "Comp admin can mark projects to create")
    ServiceResult<Void> markApplicationReadyToBeCreated(long applicationId, String emailBody);
}
