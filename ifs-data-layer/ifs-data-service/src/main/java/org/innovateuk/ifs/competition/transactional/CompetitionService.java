package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of Competitions
 */
public interface CompetitionService {
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<CompetitionResource> getCompetitionById(final long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionResource>> findAll();

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
    ServiceResult<List<CompetitionOpenQueryResource>> findAllOpenQueries(long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "GET_OPEN_QUERIES", description = "Project finance users can count open queries for a competition")
    ServiceResult<Long> countAllOpenQueries(long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "GET_PENDING_SPEND_PROFILES", description = "Project finance users can get projects for which Spend Profile generation is pending, for a given competition")
    ServiceResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance')")
    @SecuredBySpring(value = "COUNT_PENDING_SPEND_PROFILES", description = "Project finance users can count projects for which Spend Profile generation is pending, for a given competition")
    ServiceResult<Long> countPendingSpendProfiles(long competitionId);

    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    @SecuredBySpring(value = "UPDATE_TERMS_AND_CONDITIONS", securedType = CompetitionResource.class,
            description = "Only Comp Admins are able to update grant terms and conditions for the given competitions")
    ServiceResult<Void> updateTermsAndConditionsForCompetition(long competitionId, long termsAndConditionsId);

    @NotSecured(value = "Anyone can download competition terms", mustBeSecuredByOtherServices = false)
    ServiceResult<FileAndContents> downloadTerms(long competitionId);
}