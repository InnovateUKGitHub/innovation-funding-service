package com.worth.ifs.category.repository;

import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface CategoryLinkRepository extends CrudRepository<CategoryLink, Long> {

    CategoryLink findByClassNameAndClassPk(@Param("className") String className, @Param("classPk") Long classPk);
    CategoryLink findByClassNameAndClassPkAndCategory_Type(@Param("className") String className, @Param("classPk") Long classPk, @Param("type") CategoryType type);

}
