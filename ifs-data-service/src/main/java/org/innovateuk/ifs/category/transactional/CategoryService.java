package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.category.resource.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CategoryService {
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance') || hasAuthority('assessor')")
    ServiceResult<List<InnovationAreaResource>> getInnovationAreas();

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance') || hasAuthority('assessor')")
    ServiceResult<List<InnovationSectorResource>> getInnovationSectors();

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance') || hasAuthority('assessor')")
    ServiceResult<List<ResearchCategoryResource>> getResearchCategories();

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<List<InnovationAreaResource>> getInnovationAreaBySector(long sectorId);
}
