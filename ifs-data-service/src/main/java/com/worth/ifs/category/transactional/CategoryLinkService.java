package com.worth.ifs.category.transactional;

import com.worth.ifs.category.domain.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CategoryLinkService {
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> addOrUpdateOrDeleteLink(String className, Long classPk, CategoryType categoryType, Long categoryId);
}
