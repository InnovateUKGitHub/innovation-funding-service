package com.worth.ifs.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.service.CompetitionsRestService;

/**
 * This class contains methods to retrieve and store {@link CompetitionResource} related data,
 * through the RestService {@link CompetitionsRestService}.
 */
// TODO DW - INFUND-1555 - get this service to return RestResults
@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    CompetitionsRestService competitionsRestService;

    @Override
    public CompetitionResource getById(Long competitionId){
        return competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public CompetitionResource create(){
        return competitionsRestService.create().getSuccessObjectOrThrowException();
    }

    @Override
    public List<CompetitionResource> getAllCompetitions() {
        return competitionsRestService.getAll().getSuccessObjectOrThrowException();
    }

    public List<CompetitionSetupSection> getCompletedCompetitionSetupSectionStatusesByCompetitionId(Long competitionId) {

        CompetitionResource competition = competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
        
        //TODO process map from competition
        
        return new ArrayList<CompetitionSetupSection>();
    }

    @Override
    public List<CompetitionTypeResource> getAllCompetitionTypes() {
        return competitionsRestService.getCompetitionTypes().getSuccessObjectOrThrowException();
    }

    @Override
    public void update(CompetitionResource competition) {
        competitionsRestService.update(competition).getSuccessObjectOrThrowException();
    }

    @Override
    public void setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section) {
        competitionsRestService.markSectionComplete(competitionId, section).getSuccessObjectOrThrowException();
    }

    @Override
    public void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section) {
        competitionsRestService.markSectionInComplete(competitionId, section).getSuccessObjectOrThrowException();
    }

    @Override
    public String generateCompetitionCode(Long competitionId, LocalDateTime openingDate) {
        return competitionsRestService.generateCompetitionCode(competitionId, openingDate).getSuccessObjectOrThrowException();
    }


}
