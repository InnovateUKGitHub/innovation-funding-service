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
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
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

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private MilestoneService milestoneService;

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
        List<Competition> competitions = competitionRepository.findLive();
        return serviceSuccess(simpleMap(competitions, this::toLiveCompetitionResult));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            List<Competition> competitions;
            if (user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)) {
                competitions = competitionRepository.findProjectSetupForInnovationLeadOrStakeholder(user.getId());
            } else {
                competitions = competitionRepository.findProjectSetup();
            }

            return serviceSuccess(competitions.stream()
                    .map(this::toProjectSetupCompetitionResult)
                    .sorted(comparing(ProjectSetupCompetitionSearchResultItem::getManageFundingEmailDate))
                    .collect(toList()));
        });
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
        List<Competition> competitions = competitionRepository.findUpcoming();
        return serviceSuccess(simpleMap(competitions, this::toUpcomingCompetitionResult));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findNonIfsCompetitions() {
        List<Competition> competitions = competitionRepository.findNonIfs();
        return serviceSuccess(simpleMap(competitions, this::toNonIfsCompetitionSearchReult));
    }

    @Override
    public ServiceResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions() {
        List<Competition> competitions = competitionRepository.findFeedbackReleased();
        return serviceSuccess(simpleMap(competitions, this::toPreviousCompetitionSearchResult).stream().sorted((c1, c2) -> c2.getOpenDate().compareTo(c1.getOpenDate())).collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
        String searchQueryLike = String.format("%%%s%%", searchQuery);
        PageRequest pageRequest = new PageRequest(page, size);
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            if (user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)) {
                return handleCompetitionSearchResultPage(pageRequest, size, competitionRepository.searchForLeadTechnologist(searchQueryLike, user.getId(), pageRequest));
            } else if (user.hasRole(SUPPORT)) {
                return handleCompetitionSearchResultPage(pageRequest, size, competitionRepository.searchForSupportUser(searchQueryLike, pageRequest));
            } else {
                return handleCompetitionSearchResultPage(pageRequest, size, competitionRepository.search(searchQueryLike, pageRequest));
            }
        });
    }

    private LiveCompetitionSearchResultItem toLiveCompetitionResult(Competition competition) {
        return new LiveCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                applicationRepository.countByCompetitionId(competition.getId())
        );
    }

    private ProjectSetupCompetitionSearchResultItem toProjectSetupCompetitionResult(Competition competition) {
        return new ProjectSetupCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                projectRepository.countByApplicationCompetitionId(competition.getId()),
                applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(competition.getId()).getManageFundingEmailDate()
        );
    }

    private UpcomingCompetitionSearchResultItem toUpcomingCompetitionResult(Competition competition) {
        return new UpcomingCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                competition.startDateDisplay()
        );
    }

    private NonIfsCompetitionSearchResultItem toNonIfsCompetitionSearchReult(Competition competition) {
        return new NonIfsCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                publicContentService.findByCompetitionId(competition.getId()).getSuccess().getPublishDate()
        );
    }

    private PreviousCompetitionSearchResultItem toPreviousCompetitionSearchResult(Competition competition) {
        ZonedDateTime openDate;
        ServiceResult<MilestoneResource> openDateMilestone = milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competition.getId());
        if (openDateMilestone.isSuccess()) {
            openDate = openDateMilestone.getSuccess().getDate();
        } else {
            openDate = null;
        }

        return new PreviousCompetitionSearchResultItem(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                ofNullable(competition.getInnovationAreas()).orElseGet(Collections::emptySet)
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toCollection(TreeSet::new)),
                ofNullable(competition.getCompetitionType()).map(CompetitionType::getName).orElse(null),
                openDate
        );
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

    private AbstractCompetitionSearchResultItem searchResultFromCompetition(Competition competition) {
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
