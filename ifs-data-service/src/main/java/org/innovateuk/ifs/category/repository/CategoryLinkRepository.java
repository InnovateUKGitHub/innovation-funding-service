package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.CategoryLink;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CategoryLinkRepository extends CrudRepository<CategoryLink, Long> {

    List<CategoryLink> findByClassNameAndClassPk(String className, Long classPk);
    List<CategoryLink> findByClassNameAndClassPkAndCategory_Type(String className, Long classPk, CategoryType type);

}
