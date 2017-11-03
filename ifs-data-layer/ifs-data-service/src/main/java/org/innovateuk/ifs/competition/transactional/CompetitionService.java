package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
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

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<List<UserResource>> findInnovationLeads(final Long competitionId);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<Void> addInnovationLead(final Long competitionId, final Long innovationLeadUserId);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'MANAGE_INNOVATION_LEADS')")
    ServiceResult<Void> removeInnovationLead(final Long competitionId, final Long innovationLeadUserId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionResource>> getCompetitionsByUserId(Long userId);

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

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions();

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_UNSUCCESSFUL_APPLICATIONS')")
    ServiceResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId, int pageIndex, int pageSize, String sortField);

    @SecuredBySpring(value = "SEARCH", description = "Only internal users can search for competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
    ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size);

    @SecuredBySpring(value = "COUNT", description = "Only internal users count competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
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

    @PreAuthorize("hasAnyAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_COMPETITION_ORGANISATION_TYPES", description = "Anyone should be able to see what organisation types are allowed in a competition")
    ServiceResult<List<OrganisationTypeResource>> getCompetitionOrganisationTypes(long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "GET_OPEN_QUERIES", description = "Project finance users can see all open queries for a competition")
    ServiceResult<List<CompetitionOpenQueryResource>> findAllOpenQueries(Long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "GET_OPEN_QUERIES", description = "Project finance users can count open queries for a competition")
    ServiceResult<Long> countAllOpenQueries(Long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "GET_PENDING_SPEND_PROFILE_GENERATION", description = "Project finance users can get projects for which Spend Profile generation is pending, for a given competition")
    ServiceResult<List<CompetitionPendingSpendProfilesResource>> getPendingSpendProfiles(Long competitionId);
}
