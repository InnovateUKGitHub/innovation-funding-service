package com.worth.ifs.application.service;

import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods to retrieve and store {@link CategoryResource} related data,
 * through the RestService {@link CategoryServiceResource}.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<CategoryResource> getCategoryByType(CategoryType type) {
        // TODO : Make use of RestService

        List<CategoryResource> categoryResources = new ArrayList();

        CategoryResource categoryResource = new CategoryResource();
        categoryResource.setId(1L);
        categoryResource.setName("Category 1");
        categoryResource.setType(type);
        categoryResources.add(categoryResource);

        categoryResource = new CategoryResource();
        categoryResource.setId(2L);
        categoryResource.setName("Category 2");
        categoryResource.setType(type);
        categoryResources.add(categoryResource);

        categoryResource = new CategoryResource();
        categoryResource.setId(3L);
        categoryResource.setName("Category 3");
        categoryResource.setType(type);
        categoryResources.add(categoryResource);

        return categoryResources;
    }
}
