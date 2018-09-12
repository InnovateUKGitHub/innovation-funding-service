package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionResearchCategoryLinkList;

@Service
public class CompetitionResearchCategoryRestServiceImpl extends BaseRestService implements CompetitionResearchCategoryRestService {

    private String url = "/competition-research-category";

    @Override
    public RestResult<List<CompetitionResearchCategoryLinkResource>> findByCompetition(Long id) {
        return getWithRestResult(url + "/" + id, competitionResearchCategoryLinkList());
    }
}
