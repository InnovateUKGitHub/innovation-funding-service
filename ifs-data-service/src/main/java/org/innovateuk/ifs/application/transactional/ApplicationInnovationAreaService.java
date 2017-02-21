package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional service for linking an {@link Application} to an {@link InnovationArea}.
 */
public interface ApplicationInnovationAreaService {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    @SecuredBySpring(value = "UPDATE", description = "Applicants can update the Innovation Area on their application.")
    ServiceResult<Application> setInnovationArea(Long applicationId, Long innovationAreaId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    @SecuredBySpring(value = "UPDATE", description = "Applicants can set that no Innovation Area applies.")
    ServiceResult<Application> setNoInnovationAreaApplies(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    @SecuredBySpring(value = "READ", description = "Applicants can view the available Innovation Areas for their application.")
    ServiceResult<List<InnovationArea>> getAvailableInnovationAreas(Long applicationId);
}
