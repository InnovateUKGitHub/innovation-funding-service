package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.springframework.security.access.method.P;
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

    @NotSecured(value = "Not secured here, because this is only added extra information on the competition instance.", mustBeSecuredByOtherServices = true)
    Competition addCategories(@P("competition") Competition competition);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionResource>> findAll();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions();

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<CompetitionCountResource> countCompetitions();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'competition_executive')")
    @SecuredBySpring(value = "CLOSE_ASSESSMENT", description = "Comp Admins can change the competition state to Assessment Closed")
    ServiceResult<Void> closeAssessment(Long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'competition_executive')")
    @SecuredBySpring(value = "NOTIFY_ASSESSORS", description = "Comp Admins can change the competition state to Assessors Notified")
    ServiceResult<Void> notifyAssessors(Long competitionId);
}
