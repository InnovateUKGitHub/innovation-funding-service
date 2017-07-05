package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CategoryService {
    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, assessor, innovation lead or system registrar roles can read innovation areas")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'assessor', 'system_registrar', 'competition_technologist')")
    ServiceResult<List<InnovationAreaResource>> getInnovationAreas();

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, innovation lead or assessor roles can read innovation sectors")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'assessor', 'competition_technologist')")
    ServiceResult<List<InnovationSectorResource>> getInnovationSectors();

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, assessor, innovation lead or applicant roles can read research categories")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'assessor', 'applicant', 'competition_technologist')")
    ServiceResult<List<ResearchCategoryResource>> getResearchCategories();

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, innovation lead or support roles can read innovation areas by sector")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'competition_technologist')")
    ServiceResult<List<InnovationAreaResource>> getInnovationAreasBySector(long sectorId);
}
