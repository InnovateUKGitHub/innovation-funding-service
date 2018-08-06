package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;

import java.util.List;

public interface CompetitionResearchCategoryService {

    @NotSecured(value = "Any user can see the latest site terms and conditions",
            mustBeSecuredByOtherServices = false)
    ServiceResult<List<CompetitionResearchCategoryLinkResource>> findByCompetition(Long competitionId);
}
