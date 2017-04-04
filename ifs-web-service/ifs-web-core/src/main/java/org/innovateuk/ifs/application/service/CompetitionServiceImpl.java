package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.service.ContentGroupRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link CompetitionResource} related data,
 * through the RestService {@link CompetitionsRestService}.
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {

    public static final int COMPETITION_PAGE_SIZE = 20;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @Autowired
    private ContentGroupRestService contentGroupRestService;

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
    public ServiceResult<Void> setSetupSectionMarkedAsIncomplete(Long competitionId, CompetitionSetupSection section) {
        return competitionsRestService.markSectionInComplete(competitionId, section).toServiceResult();
    }

    @Override
    public ServiceResult<Void> initApplicationFormByCompetitionType(Long competitionId, Long competitionTypeId) {
        return competitionsRestService.initApplicationForm(competitionId, competitionTypeId).toServiceResult();
    }

    @Override
    public String generateCompetitionCode(Long competitionId, ZonedDateTime openingDate) {
        return competitionsRestService.generateCompetitionCode(competitionId, openingDate).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> returnToSetup(Long competitionId) {
        return competitionsRestService.returnToSetup(competitionId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> markAsSetup(Long competitionId) {
        return competitionsRestService.markAsSetup(competitionId).toServiceResult();
    }

    @Override
    public List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId) {
        return assessorCountOptionsRestService.findAllByCompetitionType(competitionTypeId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> closeAssessment(Long competitionId) {
        return competitionsRestService.closeAssessment(competitionId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> notifyAssessors(Long competitionId) {
        return competitionsRestService.notifyAssessors(competitionId).toServiceResult();
    }

    @Override
    public void releaseFeedback(Long competitionId) {
        competitionsRestService.releaseFeedback(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public PublicContentItemResource getPublicContentOfCompetition(Long competitionId) {
        return publicContentItemRestService.getItemByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public ByteArrayResource downloadPublicContentAttachment(Long contentGroupId) {
        return contentGroupRestService.getFileAnonymous(contentGroupId).getSuccessObjectOrThrowException();
    }

    @Override
    public FileEntryResource getPublicContentFileDetails(Long contentGroupId) {
        return contentGroupRestService.getFileDetailsAnonymous(contentGroupId).getSuccessObjectOrThrowException();
    }

    @Override
    public CompetitionResource createNonIfs() {
        return competitionsRestService.createNonIfs().getSuccessObjectOrThrowException();
    }
}
