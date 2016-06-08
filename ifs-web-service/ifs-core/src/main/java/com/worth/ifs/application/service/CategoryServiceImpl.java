package com.worth.ifs.application.service;

import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.service.CategoryRestService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
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
}
