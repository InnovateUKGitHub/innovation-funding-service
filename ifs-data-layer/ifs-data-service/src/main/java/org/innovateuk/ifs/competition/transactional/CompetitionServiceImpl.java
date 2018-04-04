package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.invite.domain.competition.AssessmentParticipant;
import org.innovateuk.ifs.invite.domain.competition.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.competition.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isInnovationLead;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isSupport;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrganisationTypeMapper organisationTypeMapper;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private CompetitionKeyStatisticsService competitionKeyStatisticsService;

    @Autowired
    private MilestoneService milestoneService;

    private static final Map<String, Sort> APPLICATION_SORT_FIELD_MAP = new HashMap<String, Sort>() {{
        put("id", new Sort(ASC, "id"));
        put("name", new Sort(ASC, "name", "id"));
    }};

    private static final String EOI = "Expression of interest";

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id)).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    @Override
    public ServiceResult<List<UserResource>> findInnovationLeads(Long competitionId) {

        List<AssessmentParticipant> competitionParticipants = competitionParticipantRepository.getByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.INNOVATION_LEAD);

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
                    AssessmentParticipant competitionParticipant = new AssessmentParticipant();
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
        List<ApplicationResource> userApplications = applicationService.findByUserId(userId).getSuccess();
        List<Long> competitionIdsForUser = userApplications.stream()
                .map(ApplicationResource::getCompetition)
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

    private ZonedDateTime findMostRecentFundingInformDate(Competition competition) {
        return competition.getApplications()
                .stream()
                .filter(application -> application.getManageFundingEmailDate() != null)
                .max(Comparator.comparing(Application::getManageFundingEmailDate))
                .get().getManageFundingEmailDate();
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            List<Competition> competitions;
            if (user.hasRole(INNOVATION_LEAD)) {
                competitions = competitionRepository.findProjectSetupForInnovationLead(user.getId());
            } else {
                competitions = competitionRepository.findProjectSetup();
            }
            // Only competitions with at least one funded and informed application can be considered as in project setup
            return serviceSuccess(simpleMap(
                    CollectionFunctions.reverse(competitions.stream()
                            .filter(competition -> !competition.getCompetitionType().getName().equals(EOI))
                            .map(competition -> Pair.of(findMostRecentFundingInformDate(competition), competition))
                            .sorted(Comparator.comparing(Pair::getKey))
                            .map(Pair::getValue)
                            .collect(Collectors.toList())),
                    this::searchResultFromCompetition));
        });
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
    public ServiceResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions() {
        List<Competition> competitions = competitionRepository.findFeedbackReleased();
        return serviceSuccess(simpleMap(competitions, this::searchResultFromCompetition).stream().sorted((c1, c2) -> c2.getOpenDate().compareTo(c1.getOpenDate())).collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId,
                                                                               int pageIndex,
                                                                               int pageSize,
                                                                               String sortField) {

        Set<State> unsuccessfulStates = simpleMapSet(asLinkedSet(
                INELIGIBLE,
                INELIGIBLE_INFORMED,
                REJECTED), ApplicationState::getBackingState);

        Sort sort = getApplicationSortField(sortField);
        Pageable pageable = new PageRequest(pageIndex, pageSize, sort);

        Page<Application> pagedResult = applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, unsuccessfulStates, pageable);
        List<ApplicationResource> unsuccessfulApplications = simpleMap(pagedResult.getContent(), this::convertToApplicationResource);

        return serviceSuccess(new ApplicationPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), unsuccessfulApplications, pagedResult.getNumber(), pagedResult.getSize()));
    }

    private Sort getApplicationSortField(String sortBy) {
        Sort result = APPLICATION_SORT_FIELD_MAP.get(sortBy);
        return result != null ? result : APPLICATION_SORT_FIELD_MAP.get("id");
    }

    private ApplicationResource convertToApplicationResource(Application application) {

        ApplicationResource applicationResource = applicationMapper.mapToResource(application);
        Organisation leadOrganisation = organisationRepository.findOne(application.getLeadOrganisationId());
        applicationResource.setLeadOrganisationName(leadOrganisation.getName());
        return applicationResource;
    }

    @Override
    public ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        String searchQueryLike = String.format("%%%s%%", searchQuery);
        PageRequest pageRequest = new PageRequest(page, size);
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            if (user.hasRole(INNOVATION_LEAD)) {
                return handleCompetitionSearchResultPage(pageRequest, size, competitionRepository.searchForLeadTechnologist(searchQueryLike, user.getId(), pageRequest));
            } else if (user.hasRole(SUPPORT)) {
                return handleCompetitionSearchResultPage(pageRequest, size, competitionRepository.searchForSupportUser(searchQueryLike, pageRequest));
            } else {
                return handleCompetitionSearchResultPage(pageRequest, size, competitionRepository.search(searchQueryLike, pageRequest));
            }
        });
    }

    private ServiceResult<CompetitionSearchResult> handleCompetitionSearchResultPage(PageRequest pageRequest, int size, Page<Competition> pageResult) {
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
        ZonedDateTime openDate;
        ServiceResult<MilestoneResource> openDateMilestone = milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, c.getId());
        if (openDateMilestone.isSuccess()) {
            openDate = openDateMilestone.getSuccess().getDate();
        } else {
            openDate = null;
        }
        return getCurrentlyLoggedInUser().andOnSuccess(currentUser -> serviceSuccess(new CompetitionSearchResultItem(c.getId(),
                c.getName(),
                ofNullable(c.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                applicationRepository.countByCompetitionId(c.getId()),
                c.startDateDisplay(),
                c.getCompetitionStatus(),
                ofNullable(c.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                projectRepository.findByApplicationCompetitionId(c.getId()).size(),
                publicContentService.findByCompetitionId(c.getId()).getSuccess().getPublishDate(),
                isSupport(currentUser) ? "/competition/" + c.getId() + "/applications/all" : "/competition/" + c.getId(),
                openDate
        ))).getSuccess();
    }

    @Override
    public ServiceResult<CompetitionCountResource> countCompetitions() {
        //TODO INFUND-3833 populate complete count
        return serviceSuccess(
                new CompetitionCountResource(
                        getLiveCount(),
                        getPSCount(),
                        competitionRepository.countUpcoming(),
                        getFeedbackReleasedCount(),
                        competitionRepository.countNonIfs()));
    }

    private Long getLiveCount(){
        return getCurrentlyLoggedInUser().andOnSuccessReturn(user ->
                isInnovationLead(user) ?
                        competitionRepository.countLiveForInnovationLead(user.getId()) : competitionRepository.countLive()
        ).getSuccess();
    }

    private Long getPSCount(){
        return getCurrentlyLoggedInUser().andOnSuccessReturn(user ->
                isInnovationLead(user) ?
                        competitionRepository.countProjectSetupForInnovationLead(user.getId()) : competitionRepository.countProjectSetup()
        ).getSuccess();
    }

    private Long getFeedbackReleasedCount(){
        return getCurrentlyLoggedInUser().andOnSuccessReturn(user ->
                isInnovationLead(user) ?
                        competitionRepository.countFeedbackReleasedForInnovationLead(user.getId()) : competitionRepository.countFeedbackReleased()
        ).getSuccess();
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
                        .getSuccess();
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
                        .getSuccess();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId);
            competition.setFundersPanelEndDate(ZonedDateTime.now());
        }
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<CompetitionOpenQueryResource>> findAllOpenQueries(Long competitionId) {
        return serviceSuccess(competitionRepository.getOpenQueryByCompetition(competitionId));
    }

    @Override
    public ServiceResult<Long> countAllOpenQueries(Long competitionId) {
        return serviceSuccess(competitionRepository.countOpenQueries(competitionId));
    }

    @Override
    public ServiceResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(Long competitionId) {

        List<Object[]> pendingSpendProfiles = competitionRepository.getPendingSpendProfiles(competitionId);
        return serviceSuccess(simpleMap(pendingSpendProfiles, object ->
                new SpendProfileStatusResource(((BigInteger)object[0]).longValue(), ((BigInteger)object[1]).longValue(), (String)object[2])));
    }

    @Override
    public ServiceResult<Long> countPendingSpendProfiles(Long competitionId) {

        return serviceSuccess(competitionRepository.countPendingSpendProfiles(competitionId).longValue());
    }
}
