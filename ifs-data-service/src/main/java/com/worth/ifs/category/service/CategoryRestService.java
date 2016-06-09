package com.worth.ifs.category.service;

import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Rest service to retrieve and update Categories.
 */
public interface CategoryRestService {
    RestResult<List<CategoryResource>> getByType(CategoryType type);

    RestResult<List<CategoryResource>> getByParent(Long parentId);
}
