package com.worth.ifs.category.repository;

import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CategoryLinkRepository extends CrudRepository<CategoryLink, Long> {

    List<CategoryLink> findByClassNameAndClassPk(String className, Long classPk);
    CategoryLink findByClassNameAndClassPkAndCategory_Type(String className, Long classPk, CategoryType type);

}