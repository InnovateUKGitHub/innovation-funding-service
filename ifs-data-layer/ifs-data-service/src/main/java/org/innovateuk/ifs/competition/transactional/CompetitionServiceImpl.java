package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isSupport;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    private static final Log LOG = LogFactory.getLog(CompetitionServiceImpl.class);

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrganisationTypeMapper organisationTypeMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private CompetitionKeyStatisticsService competitionKeyStatisticsService;

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id)).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    @Override
    public ServiceResult<List<UserResource>> findInnovationLeads(Long competitionId) {

        List<CompetitionParticipant> competitionParticipants = competitionParticipantRepository.getByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.INNOVATION_LEAD);

        List<UserResource> innovationLeads = simpleMap(competitionParticipants, competitionParticipant -> userMapper.mapToResource(competitionParticipant.getUser()));

        return serviceSuccess(innovationLeads);
    }

    @Override
    @Transactional
    public ServiceResult<Void> addInnovationLead(Long competitionId, Long innovationLeadUserId) {

        return find(competitionRepository.findById(competitionId),
                    notFoundError(Competition.class, competitionId))
            .andOnSuccessReturnVoid(competition -> {
                find(userRepository.findOne(innovationLeadUserId),
                     notFoundError(User.class, innovationLeadUserId))
                .andOnSuccess(innovationLead -> {
                    CompetitionParticipant competitionParticipant = new CompetitionParticipant();
                    competitionParticipant.setProcess(competition);
                    competitionParticipant.setUser(innovationLead);
                    competitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
                    competitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

                    competitionParticipantRepository.save(competitionParticipant);

                    return serviceSuccess();
                });
            });
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeInnovationLead(Long competitionId, Long innovationLeadUserId) {

        return find(competitionParticipantRepository.getByCompetitionIdAndUserIdAndRole(competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD),
                    notFoundError(CompetitionParticipant.class, competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD))
                .andOnSuccessReturnVoid(competitionParticipant -> competitionParticipantRepository.delete(competitionParticipant));
    }

    @Override
    public ServiceResult<List<CompetitionResource>> getCompetitionsByUserId(Long userId) {
        List<ApplicationResource> userApplications = applicationService.findByUserId(userId).getSuccessObjectOrThrowException();
        List<Long> competitionIdsForUser = userApplications.stream()
                .map(applicationResource -> applicationResource.getCompetition())
                .distinct()
                .collect(Collectors.toList());

        return serviceSuccess((List) competitionMapper.mapToResource(
                competitionRepository.findByIdIsIn(competitionIdsForUser))
        );
    }

    @Override
    public ServiceResult<List<OrganisationTypeResource>> getCompetitionOrganisationTypes(long id) {
        return find(competitionRepository.findById(id), notFoundError(OrganisationType.class, id)).andOnSuccess(comp -> serviceSuccess((List) organisationTypeMapper.mapToResource(comp.getLeadApplicantTypes())));
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(
                competitionRepository.findAll().stream().filter(comp -> !comp.isTemplate()).collect(Collectors.toList())
        ));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
        List<Competition> competitions = competitionRepository.findLive();
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition));
    }
    
    private ZonedDateTime findMostRecentFundingInformDate(Competition c1) {
        return c1.getApplications()
                .stream()
                .filter(a -> a.getManageFundingEmailDate() != null)
                .max(Comparator.comparing(a -> a.getManageFundingEmailDate()))
                .get().getManageFundingEmailDate();
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
        List<Competition> competitions = competitionRepository.findProjectSetup();
        // IFS-1620 only competitions with at least one funded and informed application can be considered as in project setup
        return serviceSuccess(simpleMap(
                CollectionFunctions.reverse(competitions.stream()
                    .map(c -> Pair.of(findMostRecentFundingInformDate(c), c))
                    .sorted(Comparator.comparing(p -> p.getKey()))
                    .map(p -> p.getValue())
                    .collect(Collectors.toList())),
                this::searchResultFromCompetition));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
        List<Competition> competitions = competitionRepository.findUpcoming();
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findNonIfsCompetitions() {
        List<Competition> competitions = competitionRepository.findNonIfs();
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition));
    }

    @Override
    public ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        String searchQueryLike = String.format("%%%s%%", searchQuery);
        PageRequest pageRequest = new PageRequest(page, size);
        Page<Competition> pageResult = competitionRepository.search(searchQueryLike, pageRequest);

        CompetitionSearchResult result = new CompetitionSearchResult();
        List<Competition> competitions = pageResult.getContent();
        result.setContent(simpleMap(competitions, this::searchResultFromCompetition));
        result.setNumber(pageRequest.getPageNumber());
        result.setSize(size);
        result.setTotalElements(pageResult.getTotalElements());
        result.setTotalPages(pageResult.getTotalPages());

        return serviceSuccess(result);
    }

    private CompetitionSearchResultItem searchResultFromCompetition(Competition c) {
        return getCurrentlyLoggedInUser().andOnSuccess(currentUser -> serviceSuccess(new CompetitionSearchResultItem(c.getId(),
                c.getName(),
                ofNullable(c.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                c.getApplications().size(),
                c.startDateDisplay(),
                c.getCompetitionStatus(),
                ofNullable(c.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                projectRepository.findByApplicationCompetitionId(c.getId()).size(),
                publicContentService.findByCompetitionId(c.getId()).getSuccessObjectOrThrowException().getPublishDate(),
                isSupport(currentUser) ? "/competition/" + c.getId() + "/applications/all" : "/competition/" + c.getId()
        ))).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<CompetitionCountResource> countCompetitions() {
        //TODO INFUND-3833 populate complete count
        return serviceSuccess(new CompetitionCountResource(competitionRepository.countLive(), competitionRepository.countProjectSetup(),
                competitionRepository.countUpcoming(), 0L, competitionRepository.countNonIfs()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> closeAssessment(long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.closeAssessment(ZonedDateTime.now());
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyAssessors(long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.notifyAssessors(ZonedDateTime.now());
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> releaseFeedback(long competitionId) {
        CompetitionFundedKeyStatisticsResource keyStatisticsResource =
                competitionKeyStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccessObjectOrThrowException();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId);
            competition.releaseFeedback(ZonedDateTime.now());
            return serviceSuccess();
        } else {
            return serviceFailure(new Error(COMPETITION_CANNOT_RELEASE_FEEDBACK));
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> manageInformState(long competitionId) {
        CompetitionFundedKeyStatisticsResource keyStatisticsResource =
                competitionKeyStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccessObjectOrThrowException();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId);
            competition.setFundersPanelEndDate(ZonedDateTime.now());
        }
        return serviceSuccess();
    }
}
