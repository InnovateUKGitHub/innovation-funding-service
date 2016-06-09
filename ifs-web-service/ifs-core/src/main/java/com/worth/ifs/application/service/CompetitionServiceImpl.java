package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        List<CompetitionSetupCompletedSectionResource> completedSections = competitionsRestService.getCompletedSetupSections(competitionId).getSuccessObjectOrThrowException();

        List<Long> completedSectionIds = completedSections.stream()
                .map(competitionSetupCompletedSectionResource -> competitionSetupCompletedSectionResource.getCompetitionSetupSection())
                .collect(Collectors.toList());

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

    @Override
    public String generateCompetitionCode(Long competitionId, LocalDateTime openingDate) {
        return competitionsRestService.generateCompetitionCode(competitionId, openingDate).getSuccessObjectOrThrowException();
    }


}
