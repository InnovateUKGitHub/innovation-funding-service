package org.innovateuk.ifs.category.controller;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for finding generic categories by type or parentId
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;


    @RequestMapping("/findByType/{type}")
    public RestResult<List<CategoryResource>> findByType(@PathVariable("type") final String type){
        return categoryService.getByType(CategoryType.fromString(type)).toGetResponse();
    }

    @RequestMapping("/findByParent/{parentId}")
    public RestResult<List<CategoryResource>> findByParent(@PathVariable("parentId") final Long parentId){
        return categoryService.getByParent(parentId).toGetResponse();
    }

}
