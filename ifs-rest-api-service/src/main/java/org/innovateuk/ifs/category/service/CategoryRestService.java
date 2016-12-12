package org.innovateuk.ifs.category.service;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Rest service to retrieve and update Categories.
 */
public interface CategoryRestService {
    RestResult<List<CategoryResource>> getByType(CategoryType type);

    RestResult<List<CategoryResource>> getByParent(Long parentId);
}
