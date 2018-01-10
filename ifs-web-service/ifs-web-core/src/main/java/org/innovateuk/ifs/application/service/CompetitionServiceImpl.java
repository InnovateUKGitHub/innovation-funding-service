package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.service.ContentGroupRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link CompetitionResource} related data,
 * through the RestService {@link CompetitionRestService}.
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {

    public static final int COMPETITION_PAGE_SIZE = 20;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @Autowired
    private ContentGroupRestService contentGroupRestService;

    @Autowired
    private AssessorCountOptionsRestService assessorCountOptionsRestService;

    @Override
    public CompetitionResource getById(Long competitionId){
        return competitionRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<UserResource> findInnovationLeads(Long competitionId){
        return competitionRestService.findInnovationLeads(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public void addInnovationLead(Long competitionId, Long innovationLeadUserId){
        competitionRestService.addInnovationLead(competitionId, innovationLeadUserId);
    }

    @Override
    public void removeInnovationLead(Long competitionId, Long innovationLeadUserId){
        competitionRestService.removeInnovationLead(competitionId, innovationLeadUserId);
    }

    @Override
    public CompetitionResource getPublishedById(Long competitionId){
        return competitionRestService.getPublishedCompetitionById(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<CompetitionResource> getAllCompetitions() {
        return competitionRestService.getAll().getSuccessObjectOrThrowException();
    }

    @Override
    public List<CompetitionResource> getAllCompetitionsNotInSetup() {
        List<CompetitionResource> competitions = competitionRestService.getAll().getSuccessObjectOrThrowException();

        return competitions
                .stream()
                .filter(competition -> competition.getCompetitionStatus() == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganisationTypeResource> getOrganisationTypes(long id) {
        return competitionRestService.getCompetitionOrganisationType(id).getSuccessObjectOrThrowException();
    }

    @Override
    public List<CompetitionTypeResource> getAllCompetitionTypes() {
        return competitionRestService.getCompetitionTypes().getSuccessObjectOrThrowException();
    }

    @Override
    public List<AssessorCountOptionResource> getAssessorOptionsForCompetitionType(Long competitionTypeId) {
        return assessorCountOptionsRestService.findAllByCompetitionType(competitionTypeId).getSuccessObjectOrThrowException();
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
}
