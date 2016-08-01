package com.worth.ifs.category.repository;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;


public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType type, String className, Long classPk);
    Set<Category> findAllByTypeAndCategoryLinks_ClassNameAndCategoryLinks_ClassPk(CategoryType researchCategory,
			String competitionClassName, Long id);
    List<Category> findByType(CategoryType type);
	
}
