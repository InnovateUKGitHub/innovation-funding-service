package com.worth.ifs.application.service;

import java.util.List;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link Competition} related data,
 * through the RestService {@link CompetitionsRestService}.
 */
// TODO DW - INFUND-1555 - get this service to return RestResults
@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    CompetitionsRestService competitionsRestService;

    @Override
    public CompetitionResource getById(Long competitionId){
        return competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrNull();
    }

    @Override
    public List<CompetitionResource> getAllCompetitions() {
        return competitionsRestService.getAll().getSuccessObjectOrNull();
   }
}
