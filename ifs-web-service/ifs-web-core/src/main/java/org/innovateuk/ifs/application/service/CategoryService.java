package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for operations on {@link CompetitionResource} related data.
 */
@Service
public interface CategoryService {
    List<CategoryResource> getCategoryByType(CategoryType type);

    List<CategoryResource> getCategoryByParentId(Long categoryParentId);
}
