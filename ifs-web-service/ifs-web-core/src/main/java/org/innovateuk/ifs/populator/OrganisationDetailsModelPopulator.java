package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * View model for Organisation Details
 */
@Component
public class OrganisationDetailsModelPopulator {

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    protected InviteRestService inviteRestService;

    @Autowired
    protected OrganisationRestService organisationRestService;

    public void populateModel(final Model model, final Long applicationId) {
        final List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        populateModel(model, applicationId, userApplicationRoles);
    }

    public void populateModel(final Model model, final Long applicationId, final List<ProcessRoleResource> userApplicationRoles) {
        final SortedSet<OrganisationResource> organisations = getApplicationOrganisations(userApplicationRoles);
        model.addAttribute("academicOrganisations", getAcademicOrganisations(organisations));
        model.addAttribute("applicationOrganisations", organisations);

        final List<String> activeApplicationOrganisationNames = organisations.stream().map(OrganisationResource::getName).collect(Collectors.toList());

        final List<String> pendingOrganisationNames = pendingInvitations(applicationId).stream()
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        model.addAttribute("pendingOrganisationNames", pendingOrganisationNames);

        final Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );
    }

    private SortedSet<OrganisationResource> getAcademicOrganisations(final SortedSet<OrganisationResource> organisations) {
        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        final Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        final ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
                .filter(o -> OrganisationTypeEnum.RESEARCH.getId().equals(o.getOrganisationType()))
                .collect(Collectors.toCollection(supplier));
    }

    private SortedSet<OrganisationResource> getApplicationOrganisations(final List<ProcessRoleResource> userApplicationRoles) {
        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                        || uar.getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
                .collect(Collectors.toCollection(supplier));
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(final List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
                .findFirst();
    }

    private List<ApplicationInviteResource> pendingInvitations(final Long applicationId) {
        final RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(applicationId);

        return pendingAssignableUsersResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(0),
                success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                        .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                        .collect(Collectors.toList()));
    }
}
