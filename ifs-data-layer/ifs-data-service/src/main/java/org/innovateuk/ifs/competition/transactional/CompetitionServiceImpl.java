package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private OrganisationTypeMapper organisationTypeMapper;

    @Autowired
    private CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProjectToBeCreatedService projectToBeCreatedService;

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(long id) {
        return findCompetitionById(id).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    private ServiceResult<Competition> findCompetitionById(long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id));
    }

    @Override
    public ServiceResult<CompetitionResource> getCompetitionByApplicationId(long applicationId) {
        return findCompetitionByApplicationId(applicationId).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    private ServiceResult<Competition> findCompetitionByApplicationId(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(app -> app.getCompetition());
    }

    @Override
    public ServiceResult<CompetitionResource> getCompetitionByProjectId(long projectId) {
        return findCompetitionByProjectId(projectId).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    private ServiceResult<Competition> findCompetitionByProjectId(long projectId) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId))
                .andOnSuccessReturn(proj -> proj.getApplication().getCompetition());
    }

    @Override
    public ServiceResult<List<OrganisationTypeResource>> getCompetitionOrganisationTypes(long id) {
        return find(competitionRepository.findById(id), notFoundError(OrganisationType.class, id)).andOnSuccess(comp -> serviceSuccess((List) organisationTypeMapper.mapToResource(comp.getLeadApplicantTypes())));
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(
                competitionRepository.findAll()
        ));
    }

    @Override
    @Transactional
    public ServiceResult<Void> closeAssessment(long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        ServiceResult<?> result = serviceSuccess();
        if (competition.isKtp()) {
            result = aggregate(applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, newArrayList(ApplicationState.SUBMITTED))
                    .stream()
                    .map(Application::getId)
                    .map(id -> projectToBeCreatedService.markApplicationReadyToBeCreated(id, null))
                    .collect(toList()));
        }
        competition.closeAssessment(ZonedDateTime.now());
        return result.andOnSuccessReturnVoid();
    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyAssessors(long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        competition.notifyAssessors(ZonedDateTime.now());
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> releaseFeedback(long competitionId) {
        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccess();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId).get();
            competition.releaseFeedback(ZonedDateTime.now());
            return serviceSuccess();
        } else {
            return serviceFailure(new Error(COMPETITION_CANNOT_RELEASE_FEEDBACK));
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> manageInformState(long competitionId) {
        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccess();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId).get();
            competition.setFundersPanelEndDate(ZonedDateTime.now());
        }
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<CompetitionOpenQueryResource>> findAllOpenQueries(long competitionId) {
        return serviceSuccess(competitionRepository.getOpenQueryByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE)));
    }

    @Override
    public ServiceResult<Long> countAllOpenQueries(long competitionId) {
        return serviceSuccess(competitionRepository.countOpenQueriesByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE)));
    }

    @Override
    public ServiceResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(long competitionId) {

        List<Object[]> pendingSpendProfiles = competitionRepository.getPendingSpendProfiles(competitionId);
        return serviceSuccess(simpleMap(pendingSpendProfiles, object ->
                new SpendProfileStatusResource(((BigInteger) object[0]).longValue(), ((BigInteger) object[1]).longValue(), (String) object[2])));
    }

    @Override
    public ServiceResult<Long> countPendingSpendProfiles(long competitionId) {

        return serviceSuccess(competitionRepository.countPendingSpendProfiles(competitionId).longValue());
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateTermsAndConditionsForCompetition(long competitionId, long termsAndConditionsId) {
        Optional<GrantTermsAndConditions> termsAndConditions = grantTermsAndConditionsRepository.findById(termsAndConditionsId);
        if (termsAndConditions.isPresent()) {
            return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                    .andOnSuccess(competition -> {
                        competition.setTermsAndConditions(termsAndConditions.get());
                        competitionRepository.save(competition);
                        return serviceSuccess();
                    });
        }
        return serviceFailure(notFoundError(GrantTermsAndConditions.class, termsAndConditionsId));
    }

    @Override
    public ServiceResult<FileAndContents> downloadTerms(long competitionId) {
        return findCompetitionById(competitionId)
                .andOnSuccess(c -> find(c.getCompetitionTerms(), notFoundError(FileEntry.class))
                        .andOnSuccess(fe -> fileEntryService.findOne(fe.getId()))
                        .andOnSuccess(fe -> fileService.getFileByFileEntryId(fe.getId())
                                .andOnSuccessReturn(is -> new BasicFileAndContents(fe, is))
                        )
                );
    }
}
