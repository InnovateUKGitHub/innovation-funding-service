package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.category.resource.*;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link CategoryResource} related data,
 * through the RestService {@link CategoryRestService}.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public List<InnovationAreaResource> getInnovationAreas() {
        return categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException();
    }

    @Override
    public List<InnovationSectorResource> getInnovationSectors() {
        return categoryRestService.getInnovationSectors().getSuccessObjectOrThrowException();
    }

    @Override
    public List<ResearchCategoryResource> getResearchCategories() {
        return categoryRestService.getResearchCategories().getSuccessObjectOrThrowException();
    }

    @Override
    public List<InnovationAreaResource> getInnovationAreasBySector(long sectorId) {

        // zero is the Open sector so all innovation areas are relevant
        if (sectorId == 0) {
            return categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException();
        } else {
            return categoryRestService.getInnovationAreasBySector(sectorId).getSuccessObjectOrThrowException();
        }
    }
}
