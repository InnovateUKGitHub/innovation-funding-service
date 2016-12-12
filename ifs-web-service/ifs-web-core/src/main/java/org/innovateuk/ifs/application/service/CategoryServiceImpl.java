package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods to retrieve and store {@link CategoryResource} related data,
 * through the RestService {@link CategoryRestService}.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public List<CategoryResource> getCategoryByType(CategoryType type) {
        return categoryRestService.getByType(type).getSuccessObjectOrThrowException();
    }

    @Override
    public List<CategoryResource> getCategoryByParentId(Long categoryParentId) {
        return categoryRestService.getByParent(categoryParentId).getSuccessObjectOrThrowException();
    }
}
