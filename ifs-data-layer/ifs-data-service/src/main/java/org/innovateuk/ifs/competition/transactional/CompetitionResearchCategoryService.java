package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;

import java.util.List;

public interface CompetitionResearchCategoryService {

    @NotSecured(value = "Any user can find the Research Category Link per competition",
            mustBeSecuredByOtherServices = false)
    ServiceResult<List<CompetitionResearchCategoryLinkResource>> findByCompetition(Long competitionId);
}
