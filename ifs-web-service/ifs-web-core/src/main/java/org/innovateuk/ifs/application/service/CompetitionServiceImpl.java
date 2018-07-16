package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link CompetitionResource} related data,
 * through the RestService {@link CompetitionRestService}.
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public CompetitionResource getById(Long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }
}