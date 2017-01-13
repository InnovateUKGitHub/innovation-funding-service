package org.innovateuk.ifs.category.transactional;

import java.util.Set;

import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.security.access.prepost.PreAuthorize;

import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface CompetitionCategoryLinkService {
	
    /**
     * This method is for object that only have one categorylink for this CategoryType.
     * For example, a Competition instance can only have one InnovationSector category link.
     * This method checks if there already is a CompetitionCategoryLink, if there is it updates that CompetitionCategoryLink.
     * If there is no CompetitionCategoryLink, it creates it.
     * If the param categoryId is empty, the CompetitionCategoryLink is removed if it is existing.
     * @param categoryId The ID of the Category instance. If null the (optional) existing CompetitionCategoryLink is deleted.
     * @param categoryType The type of the Category.
     * @param competition the Competition that the Category is linked to
     */
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> updateCategoryLink(Long categoryId, CategoryType categoryType, Competition competition);
    
    /**
     * This method is for object that can have multiple categorylinks for this CategoryType.
     * For example, a Competition instance can only have many ResearchCategory category links.
     * All categories of the given type, class and PK must be provided.
     * @param categoryIds The IDs of the Category instances. If empty the (optional) existing CategoryLinks are deleted.
     * @param categoryType The type of the Category.
     * @param competition the Competition that the Category is linked to
     */
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<Void> updateCategoryLinks(Set<Long> categoryIds, CategoryType categoryType, Competition competition);
}
