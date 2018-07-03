package org.innovateuk.ifs.category.service;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Rest service to retrieve and update Categories.
 */
public interface CategoryRestService {

    RestResult<List<InnovationAreaResource>> getInnovationAreas();

    RestResult<List<InnovationSectorResource>> getInnovationSectors();

    RestResult<List<ResearchCategoryResource>> getResearchCategories();

    RestResult<List<InnovationAreaResource>> getInnovationAreasBySector(long sectorId);
}
