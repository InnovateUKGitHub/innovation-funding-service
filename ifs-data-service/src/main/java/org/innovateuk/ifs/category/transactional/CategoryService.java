package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CategoryService {
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance') || hasAuthority('assessor')")
    ServiceResult<List<CategoryResource>> getByType(CategoryType type);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<List<CategoryResource>> getByParent(Long parentId);
}
