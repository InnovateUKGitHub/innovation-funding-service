package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional service for linking an {@link Application} to an {@link InnovationArea}.
 */
public interface ApplicationInnovationAreaService {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE_INNOVATION_AREA')")
    @SecuredBySpring(value = "UPDATE_INNOVATION_AREA", description = "Applicants can update the Innovation Area on their application.")
    ServiceResult<ApplicationResource> setInnovationArea(@P("applicationId") Long applicationId, Long innovationAreaId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE_INNOVATION_AREA')")
    @SecuredBySpring(value = "UPDATE_INNOVATION_AREA", description = "Applicants can set that no Innovation Area applies.")
    ServiceResult<ApplicationResource> setNoInnovationAreaApplies(@P("applicationId") Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_AVAILABLE_INNOVATION_AREAS')")
    @SecuredBySpring(value = "READ_AVAILABLE_INNOVATION_AREAS", securedType = ApplicationResource.class, description = "Applicants can view the available Innovation Areas for their application.")
    ServiceResult<List<InnovationAreaResource>> getAvailableInnovationAreas(@P("applicationId") Long applicationId);
}
