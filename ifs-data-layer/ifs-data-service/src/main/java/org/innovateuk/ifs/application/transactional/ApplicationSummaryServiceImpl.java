package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryPageMapper;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.application.resource.comparators.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
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
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

    public static final Set<ApplicationState> SUBMITTED_APPLICATION_STATES = unmodifiableSet(asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.SUBMITTED));

    public static final Set<ApplicationState> SUBMITTED_STATES = unmodifiableSet(SUBMITTED_APPLICATION_STATES);

    public static final Set<ApplicationState> NOT_SUBMITTED_STATES = unmodifiableSet(asLinkedSet(
            ApplicationState.CREATED,
            ApplicationState.OPENED));

    public static final Set<ApplicationState> INELIGIBLE_STATES = unmodifiableSet(asLinkedSet(
            ApplicationState.INELIGIBLE,
            INELIGIBLE_INFORMED));

    public static final Set<ApplicationState> CREATED_AND_OPEN_STATUSES = unmodifiableSet(asLinkedSet(
            ApplicationState.CREATED,
            ApplicationState.OPENED));

    public static final Set<ApplicationState> FUNDING_DECISIONS_MADE_STATUSES = unmodifiableSet(asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED));

    public static final Set<ApplicationState> SUBMITTED_AND_INELIGIBLE_STATES = unmodifiableSet(Stream.concat(
            SUBMITTED_STATES.stream(),
            INELIGIBLE_STATES.stream()).collect(toSet()));

    private static final Map<String, Sort> SORT_FIELD_TO_DB_SORT_FIELDS;

    private static final Map<String, Comparator<ApplicationSummaryResource>> SUMMARY_COMPARATORS;

    private static final Collection<String> FIELDS_NOT_SORTABLE_IN_DB;

    static {
        Map<String, Sort> sortFieldToDbSortFields = new HashMap<>();
        sortFieldToDbSortFields.put("name", new Sort(ASC, new String[]{"name", "id"}));
        sortFieldToDbSortFields.put("duration", new Sort(ASC, new String[]{"durationInMonths", "id"}));
        sortFieldToDbSortFields.put("percentageComplete", new Sort(DESC, "completion").and(new Sort(ASC, "id")));

        SORT_FIELD_TO_DB_SORT_FIELDS = Collections.unmodifiableMap(sortFieldToDbSortFields);

        // TODO These comparators are used to sort application after loading them in memory.
        // TODO The code currently is retrieving to many of them and this sorting should be done in the database query.
        // TODO Ideally they should all be replaced - IFS-3759
        Map<String, Comparator<ApplicationSummaryResource>> summaryComparators = new HashMap<>();
        summaryComparators.put("lead", new ApplicationSummaryResourceLeadComparator());
        summaryComparators.put("leadApplicant", new ApplicationSummaryResourceLeadApplicantComparator());
        summaryComparators.put("numberOfPartners", new ApplicationSummaryResourceNumberOfPartnersComparator());
        summaryComparators.put("grantRequested", new ApplicationSummaryResourceGrantRequestedComparator());
        summaryComparators.put("totalProjectCost", new ApplicationSummaryResourceTotalProjectCostComparator());

        SUMMARY_COMPARATORS = Collections.unmodifiableMap(summaryComparators);

        FIELDS_NOT_SORTABLE_IN_DB = SUMMARY_COMPARATORS.keySet();
    }

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
                pageable -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateNotIn(competitionId, INELIGIBLE_STATES, filterStr, pageable),
                () -> applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateNotIn(competitionId, INELIGIBLE_STATES, filterStr));
    }

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize,
            Optional<String> filter,
            Optional<FundingDecisionStatus> fundingFilter,
            Optional<Boolean> inAssessmentReviewPanel) {

        String filterString = trimFilterString(filter);

        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByApplicationStateAndFundingDecision(
                        competitionId, SUBMITTED_STATES, filterString, fundingFilter.orElse(null), inAssessmentReviewPanel.orElse(null), pageable),
                () -> applicationRepository.findByApplicationStateAndFundingDecision(
                        competitionId, SUBMITTED_STATES, filterString, fundingFilter.orElse(null), inAssessmentReviewPanel.orElse(null)));
    }

    @Override
    public ServiceResult<List<Long>> getAllSubmittedApplicationIdsByCompetitionId(
            long competitionId,
            Optional<String> filter,
            Optional<FundingDecisionStatus> fundingFilter) {
        String filterString = trimFilterString(filter);
        return serviceSuccess(applicationRepository.findApplicationIdsByApplicationStateAndFundingDecision(competitionId, SUBMITTED_STATES, filterString, fundingFilter.orElse(null), null));
    }

    @Override
    public ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(
            long competitionId,
            String sortBy,
            int pageIndex,
            int pageSize) {

        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByApplicationStateAndFundingDecision(
                        competitionId, NOT_SUBMITTED_STATES, "", null, null, pageable),
                () -> applicationRepository.findByApplicationStateAndFundingDecision(
                        competitionId, NOT_SUBMITTED_STATES, "", null, null));
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
        Set<ApplicationState> states = informFilter.map(i -> i ? singleton(INELIGIBLE_INFORMED) : singleton(INELIGIBLE)).orElse(INELIGIBLE_STATES);
        return applicationSummaries(sortBy, pageIndex, pageSize,
                pageable -> applicationRepository.findByApplicationStateAndFundingDecision(
                        competitionId, states, filterStr, null, null, pageable),
                () -> applicationRepository.findByApplicationStateAndFundingDecision(
                        competitionId, states, filterStr, null, null));
    }

    @Override
    public ServiceResult<List<PreviousApplicationResource>> getPreviousApplications(long competitionId) {
        return serviceSuccess(applicationRepository.findPrevious(competitionId));
    }

    private ServiceResult<ApplicationSummaryPageResource> applicationSummaries(
            String sortBy,
            int pageIndex,
            int pageSize,
            Function<Pageable, Page<Application>> paginatedApplicationsSupplier,
            Supplier<List<Application>> nonPaginatedApplicationsSupplier) {
        Sort sortField = getApplicationSummarySortField(sortBy);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sortField);

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
