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
        // TODO : Make use of RestService

        List<CompetitionSetupSectionResource> competitionSetupSectionResources = new ArrayList();

        CompetitionSetupSectionResource competitionSetupSection = new CompetitionSetupSectionResource();
        competitionSetupSection.setId(1L);
        competitionSetupSection.setName("Initial Details");
        competitionSetupSection.setPriority(1);
        competitionSetupSectionResources.add(competitionSetupSection);

        competitionSetupSection = new CompetitionSetupSectionResource();
        competitionSetupSection.setId(2L);
        competitionSetupSection.setName("Additional info");
        competitionSetupSection.setPriority(2);
        competitionSetupSectionResources.add(competitionSetupSection);

        competitionSetupSection = new CompetitionSetupSectionResource();
        competitionSetupSection.setId(3L);
        competitionSetupSection.setName("Eligibility");
        competitionSetupSection.setPriority(3);
        competitionSetupSectionResources.add(competitionSetupSection);

        return competitionSetupSectionResources;
    }

    @Override
    public List<Long> getCompletedCompetitionSetupSectionStatusesByCompetitionId(long competitionId) {
        // TODO : Make use of RestService
        List<Long> completedCompetitionSetupSectionStatuses = new ArrayList();

        completedCompetitionSetupSectionStatuses.add(2L);
        completedCompetitionSetupSectionStatuses.add(3L);

        return completedCompetitionSetupSectionStatuses;
    }

    @Override
    public List<CompetitionTypeResource> getAllCompetitionTypes() {
        // TODO : Make use of RestService
        List<CompetitionTypeResource> competitionTypeResources = new ArrayList();

        CompetitionTypeResource competitionTypeResource = new CompetitionTypeResource();
        competitionTypeResource.setId(1L);
        competitionTypeResource.setName("Competition Type 1");

        competitionTypeResources.add(competitionTypeResource);

        competitionTypeResource = new CompetitionTypeResource();
        competitionTypeResource.setId(2L);
        competitionTypeResource.setName("Competition Type 2");

        competitionTypeResources.add(competitionTypeResource);

        return competitionTypeResources;
    }
}
