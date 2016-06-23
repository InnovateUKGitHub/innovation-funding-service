package com.worth.ifs.category.repository;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType type, String className, Long classPk);
    List<Category> findByType(CategoryType type);
}
