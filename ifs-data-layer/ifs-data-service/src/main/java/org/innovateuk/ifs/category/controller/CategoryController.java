package org.innovateuk.ifs.category.controller;

import org.innovateuk.ifs.category.resource.*;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    private CategoryService categoryService;

    @GetMapping("/findInnovationAreas")
    public RestResult<List<InnovationAreaResource>> findInnovationAreas() {
        return categoryService.getInnovationAreas().toGetResponse();
    }

    @GetMapping("/findInnovationSectors")
    public RestResult<List<InnovationSectorResource>> findInnovationSectors() {
        return categoryService.getInnovationSectors().toGetResponse();
    }

    @GetMapping("/findResearchCategories")
    public RestResult<List<ResearchCategoryResource>> findResearchCategories() {
        return categoryService.getResearchCategories().toGetResponse();
    }

    @GetMapping("/findByInnovationSector/{sectorId}")
    public RestResult<List<InnovationAreaResource>> findInnovationAreasBySector(@PathVariable("sectorId") final long sectorId){
        return categoryService.getInnovationAreasBySector(sectorId).toGetResponse();
    }
}
