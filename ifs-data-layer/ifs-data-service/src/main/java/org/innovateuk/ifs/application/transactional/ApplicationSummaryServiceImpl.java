package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryPageMapper;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.resource.comparators.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

    public static final Set<ApplicationState> SUBMITTED_APPLICATION_STATES = asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED);

    public static final Set<State> SUBMITTED_STATES = SUBMITTED_APPLICATION_STATES
            .stream().map(ApplicationState::getBackingState).collect(toSet());

    public static final Set<State> NOT_SUBMITTED_STATES = simpleMapSet(asLinkedSet(
            ApplicationState.CREATED,
            ApplicationState.OPEN), ApplicationState::getBackingState);

    public static final Set<State> INELIGIBLE_STATES = simpleMapSet(asLinkedSet(
            ApplicationState.INELIGIBLE,
            INELIGIBLE_INFORMED), ApplicationState::getBackingState);

    public static final Set<State> CREATED_AND_OPEN_STATUSES = simpleMapSet(asLinkedSet(
            ApplicationState.CREATED,
            ApplicationState.OPEN), ApplicationState::getBackingState);

    public static final Set<State> FUNDING_DECISIONS_MADE_STATUSES = simpleMapSet(asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED), ApplicationState::getBackingState);

    public static final Set<State> SUBMITTED_AND_INELIGIBLE_STATES = Stream.concat(
            SUBMITTED_STATES.stream(),
            INELIGIBLE_STATES.stream()).collect(toSet());

    private static final Map<String, Sort> SORT_FIELD_TO_DB_SORT_FIELDS = new HashMap<String, Sort>() {{
        put("name", new Sort(ASC, new String[]{"name", "id"}));
        put("duration", new Sort(ASC, new String[]{"durationInMonths", "id"}));
        put("percentageComplete", new Sort(DESC, "completion").and(new Sort(ASC, "id")));
    }};

    // TODO These comparators are used to sort application after loading them in memory.
    // TODO The code currently is retrieving to many of them and this sorting should be done in the database query.
    // TODO Ideally they should all be replaced
    private static final Map<String, Comparator<ApplicationSummaryResource>> SUMMARY_COMPARATORS =
            new HashMap<String, Comparator<ApplicationSummaryResource>>() {{
        put("lead", new ApplicationSummaryResourceLeadComparator());
        put("leadApplicant", new ApplicationSummaryResourceLeadApplicantComparator());
        put("numberOfPartners", new ApplicationSummaryResourceNumberOfPartnersComparator());
        put("grantRequested", new ApplicationSummaryResourceGrantRequestedComparator());
        put("totalProjectCost", new ApplicationSummaryResourceTotalProjectCostComparator());
    }};

    private static final Collection<String> FIELDS_NOT_SORTABLE_IN_DB = SUMMARY_COMPARATORS.keySet();

    @Autowired
    private ApplicationSummaryMapper applicationSummaryMapper;

    @Autowired
    private ApplicationSummaryPageMapper applicationSummaryPageMapper;

    @Autowired
    private OrganisationAddressMapper organisationAddressMapper;

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize,
            Optional<String> filter) {
        String filterStr = filter.map(String::trim).orElse("");
        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(competitionId, INELIGIBLE_STATES, filterStr, pageable),
                () -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(competitionId, INELIGIBLE_STATES, filterStr));
    }

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize,
            Optional<String> filter,
            Optional<FundingDecisionStatus> fundingFilter) {

        String filterString = trimFilterString(filter);

        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                        competitionId, SUBMITTED_STATES, filterString, fundingFilter.orElse(null), pageable),
                () -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                        competitionId, SUBMITTED_STATES, filterString, fundingFilter.orElse(null)));
    }

    @Override
    public ServiceResult<List<Long>> getAllSubmittedApplicationIdsByCompetitionId(
            long competitionId,
            Optional<String> filter,
            Optional<FundingDecisionStatus> fundingFilter) {
        String filterString = trimFilterString(filter);
        return find(applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                competitionId, SUBMITTED_STATES, filterString, fundingFilter.orElse(null)), notFoundError(ApplicationSummaryResource.class))
                .andOnSuccessReturn(result -> result.stream()
                        .filter(applicationFundingDecisionIsSubmittable())
                        .map(Application::getId).collect(toList()));
    }

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize) {

        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                        competitionId, NOT_SUBMITTED_STATES, "", null, pageable),
                () -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                        competitionId, NOT_SUBMITTED_STATES, "", null));
    }

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getWithFundingDecisionApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize,
            Optional<String> filter, Optional<Boolean> sendFilter,
            Optional<FundingDecisionStatus> fundingFilter) {
        String filterStr = filter.map(String::trim).orElse("");
        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByCompetitionIdAndFundingDecisionIsNotNull(
                        competitionId,
                        filterStr,
                        sendFilter.orElse(null),
                        fundingFilter.orElse(null),
                        pageable),
                () -> applicationRepository.findByCompetitionIdAndFundingDecisionIsNotNull(
                        competitionId,
                        filterStr,
                        sendFilter.orElse(null),
                        fundingFilter.orElse(null)));
    }

    @Override
    public ServiceResult<List<Long>> getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(
            long competitionId,
            Optional<String> filter,
            Optional<Boolean> sendFilter,
            Optional<FundingDecisionStatus> fundingFilter) {
        String filterStr = filter.map(String::trim).orElse("");

        return serviceSuccess(applicationRepository.findByCompetitionIdAndFundingDecisionIsNotNull(
                competitionId,
                filterStr,
                sendFilter.orElse(null),
                fundingFilter.orElse(null))
                .stream()
                .filter(Application::applicationFundingDecisionIsChangeable)
                .map(Application::getId)
                .collect(toList()));
    }

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getIneligibleApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize,
            Optional<String> filter,
            Optional<Boolean> informFilter) {
        String filterStr = filter.map(String::trim).orElse("");
        Set<State> states = informFilter.map(i -> i ? singleton(INELIGIBLE_INFORMED.getBackingState()) : singleton(INELIGIBLE.getBackingState())).orElse(INELIGIBLE_STATES);
        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                        competitionId, states, filterStr, null, pageable),
                () -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(
                        competitionId, states, filterStr, null));
    }

    @Override
    public ServiceResult<ApplicationTeamResource> getApplicationTeamByApplicationId(long applicationId) {

        ApplicationTeamResource result = new ApplicationTeamResource();
        List<ApplicationTeamOrganisationResource> partnerOrganisations = new LinkedList<>();

        return find(applicationRepository.findOne(applicationId), notFoundError(ApplicationTeamResource.class))
                .andOnSuccess(application -> {
                    // Order organisations by lead, followed by other partners in alphabetic order
                    result.setLeadOrganisation(getTeamOrganisation(application.getLeadApplicantProcessRole().getOrganisationId(), application));

                    List<Long> organisationIds = application.getProcessRoles()
                            .stream()
                            .filter(pr -> pr.getRole().getName().equals(UserRoleType.COLLABORATOR.getName()))
                            .map(u -> u.getOrganisationId())
                            .distinct()
                            .map(oId -> Pair.of(oId, organisationRepository.findOne(oId).getName()))
                            .sorted(Comparator.comparing(Pair::getValue))
                            .map(p -> p.getKey())
                            .collect(toList());
                    organisationIds.remove(application.getLeadApplicantProcessRole().getOrganisationId()); // Remove the lead organisation
                    organisationIds.forEach(organisationId -> partnerOrganisations.add(getTeamOrganisation(organisationId, application)));

                    result.setPartnerOrganisations(partnerOrganisations);
                    return serviceSuccess(result);
                });
    }

    private ApplicationTeamOrganisationResource getTeamOrganisation(long organisationId, Application application) {
        ApplicationTeamOrganisationResource teamOrg = new ApplicationTeamOrganisationResource();
        Organisation organisation = organisationRepository.findOne(organisationId);

        teamOrg.setOrganisationName(organisation.getName());
        teamOrg.setOrganisationTypeName(organisation.getOrganisationType().getName());
        teamOrg.setRegisteredAddress(getAddressByType(organisation, OrganisationAddressType.REGISTERED));

        teamOrg.setOperatingAddress(getAddressByType(organisation, OrganisationAddressType.OPERATING));

        // Order users by lead, followed by other users in alphabetic order
        List<ApplicationTeamUserResource> users = application.getProcessRoles()
                .stream()
                .filter(pr -> pr.getOrganisationId() != null && pr.getOrganisationId() == organisationId)
                .sorted((pr1, pr2) -> {
                    if (pr1.isLeadApplicant()) {
                        return -1;
                    } else if (pr2.isLeadApplicant()) {
                        return 1;
                    } else {
                        return pr1.getUser().getName().compareTo(pr2.getUser().getName());
                    }
                })
                .map(pr -> {
                    ApplicationTeamUserResource user = new ApplicationTeamUserResource();
                    user.setLead(pr.isLeadApplicant());
                    user.setName(pr.getUser().getName());
                    user.setEmail(pr.getUser().getEmail());
                    user.setPhoneNumber(pr.getUser().getPhoneNumber());
                    return user;
                })
                .collect(toList());
        teamOrg.setUsers(users);
        return teamOrg;
    }

    private OrganisationAddressResource getAddressByType(Organisation organisation, OrganisationAddressType addressType) {
        return organisationAddressMapper.mapToResource(
                organisation.getAddresses()
                        .stream()
                        .filter(a -> a.getAddressType().getName().equals(addressType.name()))
                        .findFirst()
                        .orElse(null)
        );
    }

    private ServiceResult<ApplicationSummaryPageResource> applicationSummaries(
            String sortBy,
            int pageIndex,
            int pageSize,
            Function<Pageable, Page<Application>> paginatedApplicationsSupplier,
            Supplier<List<Application>> nonPaginatedApplicationsSupplier) {
        Sort sortField = getApplicationSummarySortField(sortBy);
        Pageable pageable = new PageRequest(pageIndex, pageSize, sortField);

        if (canUseSpringDataPaginationForSummaryResults(sortBy)) {
            Page<Application> applicationResults = paginatedApplicationsSupplier.apply(pageable);
            return find(applicationResults, notFoundError(Page.class)).andOnSuccessReturn(applicationSummaryPageMapper::mapToResource);
        }

        List<Application> resultsList = nonPaginatedApplicationsSupplier.get();

        ApplicationSummaryPageResource result = new ApplicationSummaryPageResource();
        result.setContent(sortAndRestrictSummaryResults(resultsList, pageable, sortBy));

        result.setNumber(pageable.getPageNumber());
        result.setSize(pageable.getPageSize());
        result.setTotalElements(resultsList.size());
        result.setTotalPages((resultsList.size() + pageable.getPageSize() - 1) / pageable.getPageSize());
        return find(result, notFoundError(ApplicationSummaryPageResource.class));
    }

    private List<ApplicationSummaryResource> sortAndRestrictSummaryResults(
            List<Application> resultsList, Pageable pageable, String sortBy) {
        return resultsList.stream()
                .map(applicationSummaryMapper::mapToResource)
                .sorted((i1, i2) -> {
                    Comparator<ApplicationSummaryResource> comparatorForField = SUMMARY_COMPARATORS.get(sortBy);
                    if (comparatorForField == null) {
                        return 0;
                    }
                    return comparatorForField.compare(i1, i2);
                })
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(toList());
    }

    private boolean canUseSpringDataPaginationForSummaryResults(String sortBy) {
        return !FIELDS_NOT_SORTABLE_IN_DB.stream().anyMatch(field -> field.equals(sortBy));
    }

    private Sort getApplicationSummarySortField(String sortBy) {
        Sort result = SORT_FIELD_TO_DB_SORT_FIELDS.get(sortBy);
        return result != null ? result : new Sort(ASC, new String[]{"id"});
    }

    private String trimFilterString(Optional<String> filterString) {
        return filterString.map(String::trim).orElse("");
    }

    private static Predicate<Application> applicationFundingDecisionIsSubmittable() {
        return application -> application.getFundingDecision() == null || !application.getFundingDecision().equals(FundingDecisionStatus.FUNDED) ||
                application.getManageFundingEmailDate() == null;
    }
}
