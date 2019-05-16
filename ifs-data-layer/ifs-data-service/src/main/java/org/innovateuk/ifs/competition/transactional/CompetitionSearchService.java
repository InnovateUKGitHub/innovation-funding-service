package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.*;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionSearchService {
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<LiveCompetitionSearchResultItem>> findLiveCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProjectSetupCompetitionSearchResultItem>> findProjectSetupCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<UpcomingCompetitionSearchResultItem>> findUpcomingCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<NonIfsCompetitionSearchResultItem>> findNonIfsCompetitions();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<PreviousCompetitionSearchResultItem>> findFeedbackReleasedCompetitions();

    @SecuredBySpring(value = "SEARCH", description = "Only internal users can search for competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'ifs_administrator')")
    ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);

    @SecuredBySpring(value = "COUNT", description = "Only internal users count competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    ServiceResult<CompetitionCountResource> countCompetitions();

}
