package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CategoryService {
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'assessor')")
    ServiceResult<List<InnovationAreaResource>> getInnovationAreas();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'assessor')")
    ServiceResult<List<InnovationSectorResource>> getInnovationSectors();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'assessor', 'applicant')")
    ServiceResult<List<ResearchCategoryResource>> getResearchCategories();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<List<InnovationAreaResource>> getInnovationAreasBySector(long sectorId);
}
