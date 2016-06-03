package com.worth.ifs.category.repository;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.domain.CategoryType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(@Param("type") CategoryType type, @Param("className") String className, @Param("classPk") Long classPk);
}
