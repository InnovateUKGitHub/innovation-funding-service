package org.innovateuk.ifs.category.controller;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Controller for finding generic categories by type or parentId
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ZeroDowntime(reference = "IFS-4701", description = "remove findInnovationAreas")
    @GetMapping({"/findInnovationAreas", "find-innovation-areas"})
    public RestResult<List<InnovationAreaResource>> findInnovationAreas() {
        return categoryService.getInnovationAreas().toGetResponse();
    }

    @GetMapping("/find-innovation-areas-excluding-none")
    public RestResult<List<InnovationAreaResource>> findInnovationAreasExcludingNone() {
        return categoryService.getInnovationAreas()
                .andOnSuccessReturn(CategoryController::filterNoneInnovationArea)
                .toGetResponse();
    }

    private static List<InnovationAreaResource> filterNoneInnovationArea(List<InnovationAreaResource> innovationAreaResources) {
        return simpleFilter(innovationAreaResources, InnovationAreaResource::isNotNone);
    }

    @ZeroDowntime(reference = "IFS-4701", description = "remove findInnovationSectors")
    @GetMapping({"/findInnovationSectors", "find-innovation-sectors"})
    public RestResult<List<InnovationSectorResource>> findInnovationSectors() {
        return categoryService.getInnovationSectors().toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-4701", description = "remove findResearchCategories")
    @GetMapping({"/findResearchCategories", "find-research-categories"})
    public RestResult<List<ResearchCategoryResource>> findResearchCategories() {
        return categoryService.getResearchCategories().toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-4701", description = "remove findByInnovationSector")
    @GetMapping({"/findByInnovationSector/{sectorId}", "/find-by-innovation-sector/{sectorId}"})
    public RestResult<List<InnovationAreaResource>> findInnovationAreasBySector(@PathVariable("sectorId") final long sectorId){
        return categoryService.getInnovationAreasBySector(sectorId).toGetResponse();
    }
}