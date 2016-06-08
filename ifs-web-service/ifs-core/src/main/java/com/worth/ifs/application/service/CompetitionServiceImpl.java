package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<CompetitionSetupSectionResource> getCompetitionSetupSectionsByCompetitionId(long competitionId) {
        return competitionsRestService.getSetupSections().getSuccessObjectOrThrowException();
    }

    @Override
    public List<Long> getCompletedCompetitionSetupSectionStatusesByCompetitionId(long competitionId) {
        List<Long> completedSectionIds = new ArrayList();
        competitionsRestService.getCompletedSetupSections(competitionId).getSuccessObjectOrThrowException()
                .stream()
                .map(competitionSetupCompletedSectionResource -> completedSectionIds.add(competitionSetupCompletedSectionResource.getCompetitionSetupSection()));
        return completedSectionIds;
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
    public void setSetupSectionMarkedAsComplete(Long competitionId, Long sectionId) {
        competitionsRestService.markSectionComplete(competitionId, sectionId).getSuccessObjectOrThrowException();
    }

    @Override
    public void setSetupSectionMarkedAsIncomplete(Long competitionId, Long sectionId) {
        competitionsRestService.markSectionInComplete(competitionId, sectionId).getSuccessObjectOrThrowException();
    }


}
