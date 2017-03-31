package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Competitions
 */
public interface CompetitionService {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<CompetitionResource> getCompetitionById(final Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionResource>> findAll();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findNonIfsCompetitions();

    @SecuredBySpring(value = "SEARCH", description = "Only those with either comp admin or project finance roles can search for competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);

    @SecuredBySpring(value = "COUNT", description = "Only those with either comp admin or project finance roles count competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<CompetitionCountResource> countCompetitions();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "CLOSE_ASSESSMENT", description = "Comp Admins can change the competition state to Assessment Closed")
    ServiceResult<Void> closeAssessment(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "NOTIFY_ASSESSORS", description = "Comp Admins can change the competition state to Assessors Notified")
    ServiceResult<Void> notifyAssessors(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "RELEASE_FEEDBACK", description = "Comp Admins can change the competition state to Feedback Released")
    ServiceResult<Void> releaseFeedback(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "MANAGE_INFORM", description = "Comp Admins can manage the transition from Panel to Inform")
    ServiceResult<Void> manageInformState(long competitionId);
}
