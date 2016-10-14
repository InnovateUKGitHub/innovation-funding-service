package com.worth.ifs.category.transactional;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;

public interface CategoryLinkService {
	
    /**
     * This method is for object that only have one categorylink for this CategoryType.
     * For example, a Competition instance can only have one InnovationSector category link.
     * This method checks if there already is a CategoryLink, if there is it updates that CategoryLink.
     * If there is no CategoryLink, it creates it.
     * If the param categoryId is empty, the CategoryLink is removed if it is existing.
     * @param categoryId The ID of the Category instance. If null the (optional) existing CategoryLink is deleted.
     * @param categoryType The type of the Category.
     * @param className The Class name of the object to link the Category to.
     * @param classPk The Primary Key of the object to link the Category to.
     */
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> updateCategoryLink(Long categoryId, CategoryType categoryType, String className, Long classPk);
    
    /**
     * This method is for object that can have multiple categorylinks for this CategoryType.
     * For example, a Competition instance can only have many ResearchCategory category links.
     * All categories of the given type, class and PK must be provided.
     * @param categoryIds The IDs of the Category instances. If empty the (optional) existing CategoryLinks are deleted.
     * @param categoryType The type of the Category.
     * @param className The Class name of the object to link the Category to.
     * @param classPk The Primary Key of the object to link the Category to.
     */
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> updateCategoryLinks(Set<Long> categoryIds, CategoryType categoryType, String className, Long classPk);
}
