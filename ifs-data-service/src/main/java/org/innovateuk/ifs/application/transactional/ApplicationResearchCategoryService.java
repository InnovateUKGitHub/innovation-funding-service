package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional service for linking an {@link Application} to an {@link ResearchCategory}.
 */
public interface ApplicationResearchCategoryService {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE_RESEARCH_CATEGORY')")
    ServiceResult<ApplicationResource> setResearchCategory(@P("applicationId") Long applicationId, Long researchCategoryId);
}
