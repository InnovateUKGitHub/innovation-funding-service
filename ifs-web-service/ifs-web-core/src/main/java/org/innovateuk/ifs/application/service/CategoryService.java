package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.category.resource.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for operations on {@link CompetitionResource} related data.
 */
@Service
public interface CategoryService {

    List<InnovationAreaResource> getInnovationAreas();
    List<InnovationSectorResource> getInnovationSectors();
    List<ResearchCategoryResource> getResearchCategories();

    List<InnovationAreaResource> getInnovationAreasBySector(long sectorId);
}
