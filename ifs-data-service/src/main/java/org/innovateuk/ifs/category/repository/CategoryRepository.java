package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    List<Category> findByType(CategoryType type);

    List<Category> findByTypeOrderByNameAsc(CategoryType type);

    Category findByIdAndType(Long id, CategoryType type);

    Category findByNameAndType(String name, CategoryType type);
}
