package com.worth.ifs.application.service;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Competition getById(Long competitionId){
        return competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrNull();
    }

    @Override
    public List<Competition> getAllCompetitions() {
        return competitionsRestService.getAll().getSuccessObjectOrNull();
   }
}
