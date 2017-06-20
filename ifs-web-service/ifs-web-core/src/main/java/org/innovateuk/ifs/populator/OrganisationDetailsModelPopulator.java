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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * View model for Organisation Details
 */
@Component
public class OrganisationDetailsModelPopulator {

    @Autowired
    protected InviteRestService inviteRestService;

    @Autowired
    protected OrganisationRestService organisationRestService;

    public void populateModel(final Model model, final Long applicationId, final List<ProcessRoleResource> userApplicationRoles) {

        final List<OrganisationResource> organisations = getApplicationOrganisations(applicationId);
        final List<OrganisationResource> academicOrganisations = getAcademicOrganisations(organisations);
        final List<Long> academicOrganisationIds = academicOrganisations.stream().map(ao -> ao.getId()).collect(Collectors.toList());
        model.addAttribute("academicOrganisations", academicOrganisations);
        model.addAttribute("applicationOrganisations", organisations);
        Map<Long, Boolean> applicantOrganisationsAreAcademic = organisations.stream().collect(Collectors.toMap(o -> o.getId(), o -> academicOrganisationIds.contains(o.getId())));
        model.addAttribute("applicantOrganisationIsAcademic", applicantOrganisationsAreAcademic);

        final List<String> activeApplicationOrganisationNames = simpleMap(organisations, OrganisationResource::getName);

        final List<String> pendingOrganisationNames = pendingInvitations(applicationId).stream()
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        model.addAttribute("pendingOrganisationNames", pendingOrganisationNames);

        final Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles, organisations);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );
    }

    private List<OrganisationResource> getAcademicOrganisations(final List<OrganisationResource> organisations) {
        return simpleFilter(organisations, o -> OrganisationTypeEnum.RESEARCH.getId().equals(o.getOrganisationType()));
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccessObjectOrThrowException();
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(final List<ProcessRoleResource> userApplicationRoles, List<OrganisationResource> organisations) {

        Optional<ProcessRoleResource> leadApplicantRole =
                simpleFindFirst(userApplicationRoles, uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()));

        return leadApplicantRole.flatMap(role -> simpleFindFirst(organisations, org -> org.getId().equals(role.getOrganisationId())));
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
