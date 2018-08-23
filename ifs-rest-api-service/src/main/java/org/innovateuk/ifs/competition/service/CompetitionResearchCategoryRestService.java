package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;

import java.util.List;

public interface CompetitionResearchCategoryRestService {

    RestResult<List<CompetitionResearchCategoryLinkResource>> findByCompetition(Long id);
}
