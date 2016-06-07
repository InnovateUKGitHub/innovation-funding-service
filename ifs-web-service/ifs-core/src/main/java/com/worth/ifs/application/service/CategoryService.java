package com.worth.ifs.application.service;

import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
@Service
public interface CategoryService {
    List<CategoryResource> getCategoryByType(CategoryType type);
}
