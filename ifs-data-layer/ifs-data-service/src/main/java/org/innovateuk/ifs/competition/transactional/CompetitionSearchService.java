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
    ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions();

    @SecuredBySpring(value = "FIND_PROJECT_SETUP", description = "Only internal users can see project setup competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    ServiceResult<CompetitionSearchResult> findProjectSetupCompetitions(int page, int size);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions();

    @SecuredBySpring(value = "FIND_NON_IFS", description = "Only internal users can see non-ifs competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<CompetitionSearchResult> findNonIfsCompetitions(int page, int size);

    @SecuredBySpring(value = "FIND_PREVIOUS", description = "Only internal users can see previous competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    ServiceResult<CompetitionSearchResult> findPreviousCompetitions(int page, int size);

    @SecuredBySpring(value = "SEARCH", description = "Only internal users can search for competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);

    @SecuredBySpring(value = "COUNT", description = "Only internal users count competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    ServiceResult<CompetitionCountResource> countCompetitions();

}
