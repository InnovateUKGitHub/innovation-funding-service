package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.resource.search.*;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isInnovationLead;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isStakeholder;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionSearchServiceImpl extends BaseTransactionalService implements CompetitionSearchService {
    private static final String LITERAL_PERCENT = "%%";

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Autowired
    private MilestoneService milestoneService;

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
        List<Competition> competitions = competitionRepository.findLive();
        return serviceSuccess(competitions.stream()
                .map(this::toLiveCompetitionResult)
                .collect(toList()));
    }

    @Override
    public ServiceResult<CompetitionSearchResult> findProjectSetupCompetitions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            Page<Competition> competitions = user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)
                    ? competitionRepository.findProjectSetupForInnovationLeadOrStakeholder(user.getId(), pageRequest)
                    : competitionRepository.findProjectSetup(pageRequest);

            return handleCompetitionSearchResultPage(competitions, this::toProjectSetupCompetitionResult);
        });
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
        List<Competition> competitions = competitionRepository.findUpcoming();
        return serviceSuccess(competitions.stream()
                .map(this::toUpcomingCompetitionResult)
                .collect(toList()));
    }

    @Override
    public ServiceResult<CompetitionSearchResult> findNonIfsCompetitions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        Page<Competition> competitions = competitionRepository.findNonIfs(pageRequest);
        return handleCompetitionSearchResultPage(competitions, this::toNonIfsCompetitionSearchResult);
    }

    @Override
    public ServiceResult<CompetitionSearchResult> findPreviousCompetitions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        Page<Competition> competitions = competitionRepository.findFeedbackReleased(pageRequest);
        return handleCompetitionSearchResultPage(competitions, this::toPreviousCompetitionSearchResult);
    }

    @Override
    public ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        String searchQueryLike = "%" + searchQuery + "%";
        PageRequest pageRequest = PageRequest.of(page, size);
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            if (user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)) {
                return handleCompetitionSearchResultPage(competitionRepository.searchForLeadTechnologist(searchQueryLike, user.getId(), pageRequest), this::searchResultFromCompetition);
            } else if (user.hasRole(SUPPORT)) {
                return handleCompetitionSearchResultPage(competitionRepository.searchForSupportUser(searchQueryLike, pageRequest), this::searchResultFromCompetition);
            }
            return handleCompetitionSearchResultPage(competitionRepository.search(searchQueryLike, pageRequest), this::searchResultFromCompetition);
        });
    }

    private LiveCompetitionSearchResultItem toLiveCompetitionResult(Competition competition) {
        return new LiveCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                applicationRepository.countByCompetitionId(competition.getId())
        );
    }

    private ProjectSetupCompetitionSearchResultItem toProjectSetupCompetitionResult(Competition competition) {
        return new ProjectSetupCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                projectRepository.countByApplicationCompetitionId(competition.getId()),
                applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(competition.getId()).getManageFundingEmailDate()
        );
    }

    private UpcomingCompetitionSearchResultItem toUpcomingCompetitionResult(Competition competition) {
        return new UpcomingCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                competition.startDateDisplay()
        );
    }

    private NonIfsCompetitionSearchResultItem toNonIfsCompetitionSearchResult(Competition competition) {
        return new NonIfsCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                publicContentRepository.findByCompetitionId(competition.getId()).getPublishDate()
        );
    }

    private PreviousCompetitionSearchResultItem toPreviousCompetitionSearchResult(Competition competition) {
        ServiceResult<MilestoneResource> openDateMilestone = milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competition.getId());
        ZonedDateTime openDate = openDateMilestone.getOptionalSuccessObject().map(MilestoneResource::getDate).orElse(null);

        return new PreviousCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                openDate
        );
    }

    private ServiceResult<CompetitionSearchResult> handleCompetitionSearchResultPage(Page<Competition> pageResult, Function<Competition, AbstractCompetitionSearchResultItem> mapping) {
        CompetitionSearchResult result = new CompetitionSearchResult();
        List<Competition> competitions = pageResult.getContent();
        result.setContent(simpleMap(competitions, mapping));
        result.setNumber(pageResult.getPageable().getPageNumber());
        result.setSize(pageResult.getPageable().getPageSize());
        result.setTotalElements(pageResult.getTotalElements());
        result.setTotalPages(pageResult.getTotalPages());

        return serviceSuccess(result);
    }

    private AbstractCompetitionSearchResultItem searchResultFromCompetition(Competition competition) {
        if (competition.isNonIfs()) {
            return toNonIfsCompetitionSearchResult(competition);
        }
        switch (competition.getCompetitionStatus()) {
            case COMPETITION_SETUP:
            case READY_TO_OPEN:
                return toPreviousCompetitionSearchResult(competition);
            case OPEN:
            case CLOSED:
            case IN_ASSESSMENT:
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return toLiveCompetitionResult(competition);
            case PROJECT_SETUP:
                return toProjectSetupCompetitionResult(competition);
            case PREVIOUS:
                return toPreviousCompetitionSearchResult(competition);
            default:
                throw new IFSRuntimeException("Unknown competition type");
        }
    }

    @Override
    public ServiceResult<CompetitionCountResource> countCompetitions() {
        return serviceSuccess(
                new CompetitionCountResource(
                        getLiveCount(),
                        getPSCount(),
                        competitionRepository.countUpcoming(),
                        getFeedbackReleasedCount(),
                        competitionRepository.countNonIfs()));
    }

    private Long getLiveCount() {
        return getCurrentlyLoggedInUser().andOnSuccessReturn(user ->
                (isInnovationLead(user) || isStakeholder(user)) ?
                        competitionRepository.countLiveForInnovationLeadOrStakeholder(user.getId()) : competitionRepository.countLive()
        ).getSuccess();
    }

    private Long getPSCount() {
        return getCurrentlyLoggedInUser().andOnSuccessReturn(user ->
                (isInnovationLead(user) || isStakeholder(user)) ?
                        competitionRepository.countProjectSetupForInnovationLeadOrStakeholder(user.getId()) : competitionRepository.countProjectSetup()
        ).getSuccess();
    }

    private Long getFeedbackReleasedCount() {
        return getCurrentlyLoggedInUser().andOnSuccessReturn(user ->
                (isInnovationLead(user) || isStakeholder(user)) ?
                        competitionRepository.countFeedbackReleasedForInnovationLeadOrStakeholder(user.getId()) : competitionRepository.countFeedbackReleased()
        ).getSuccess();
    }
}
