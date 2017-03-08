package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional service for linking an {@link Application} to an {@link ResearchCategory}.
 */
public interface ApplicationResearchCategoryService {
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE_RESEARCH_CATEGORY')")
    ServiceResult<ApplicationResource> setResearchCategory(@P("applicationId") Long applicationId, Long researchCategoryId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_AVAILABLE_RESEARCH_CATEGORIES')")
    ServiceResult<List<ResearchCategoryResource>> getAvailableResearchCategories(@P("applicationId") Long applicationId);
}
