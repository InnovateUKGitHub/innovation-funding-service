package com.worth.ifs.category.transactional;

import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CategoryService {
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<List<CategoryResource>> getByType(CategoryType type);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<List<CategoryResource>> getByParent(Long parentId);
}
