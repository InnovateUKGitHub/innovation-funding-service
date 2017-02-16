package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link CompetitionResource} related data,
 * through the RestService {@link CompetitionsRestService}.
 */
// TODO DW - INFUND-1555 - get this service to return RestResults
@Service
public class CompetitionServiceImpl implements CompetitionService {

    public static final int COMPETITION_PAGE_SIZE = 20;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @Autowired
    private AssessorCountOptionsRestService assessorCountOptionsRestService;

    @Override
    public CompetitionResource getById(Long competitionId){
        return competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public CompetitionResource getPublishedById(Long competitionId){
        return competitionsRestService.getPublishedCompetitionById(competitionId).getSuccessObjectOrThrowException();
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
    public List<CompetitionResource> getAllCompetitionsNotInSetup() {
        List<CompetitionResource> competitions = competitionsRestService.getAll().getSuccessObjectOrThrowException();

        return competitions
                .stream()
                .filter(competition -> competition.getCompetitionStatus() == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP))
                .collect(Collectors.toList());
    }

    @Override
    public List<CompetitionTypeResource> getAllCompetitionTypes() {
        return competitionsRestService.getCompetitionTypes().getSuccessObjectOrThrowException();
    }


    @Override
    public ServiceResult<Void> update(CompetitionResource competition) {
        return competitionsRestService.update(competition).toServiceResult();
    }

    @Override
    public ServiceResult<Void> setSetupSectionMarkedAsComplete(Long competitionId, CompetitionSetupSection section) {
        return competitionsRestService.markSectionComplete(competitionId, section).toServiceResult();
    }

    @Override
    public void setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section) {
        competitionsRestService.markSectionInComplete(competitionId, section).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId) {
        return competitionsRestService.initApplicationForm(competitionId, competitionTypeId).toServiceResult();
    }

    @Override
    public String generateCompetitionCode(Long competitionId, LocalDateTime openingDate) {
        return competitionsRestService.generateCompetitionCode(competitionId, openingDate).getSuccessObjectOrThrowException();
    }

    @Override
    public void returnToSetup(Long competitionId) {
        competitionsRestService.returnToSetup(competitionId);
    }

    @Override
    public void markAsSetup(Long competitionId) {
        competitionsRestService.markAsSetup(competitionId);
    }

    @Override
    public List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId) {
        return assessorCountOptionsRestService.findAllByCompetitionType(competitionTypeId).getSuccessObjectOrThrowException();
    }

    @Override
    public void closeAssessment(Long competitionId) {
        competitionsRestService.closeAssessment(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public void notifyAssessors(Long competitionId) {
        competitionsRestService.notifyAssessors(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<PublicContentItemResource> getPublicContentOfCompetition(Long competitionId) {
        return publicContentItemRestService.getItemByCompetitionId(competitionId).toServiceResult();
    }

    @Override
    public CompetitionResource createNonIfs() {
        return competitionsRestService.createNonIfs().getSuccessObjectOrThrowException();
    }
}
