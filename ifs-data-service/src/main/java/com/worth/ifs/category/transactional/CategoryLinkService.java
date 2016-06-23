package com.worth.ifs.category.transactional;

import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CategoryLinkService {
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> updateCategoryLink(Long categoryId, CategoryType categoryType, String className, Long classPk);
}
