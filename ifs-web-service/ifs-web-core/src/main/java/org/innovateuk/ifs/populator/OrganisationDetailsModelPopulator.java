package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.viewmodel.UserApplicationRole;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

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

    protected InviteService inviteService;
    protected OrganisationRestService organisationRestService;

    public OrganisationDetailsModelPopulator(InviteService inviteService,
                                             OrganisationRestService organisationRestService) {
        this.inviteService = inviteService;
        this.organisationRestService = organisationRestService;
    }

    public void populateModel(final Model model, final Long applicationId, final List<ProcessRoleResource> userApplicationRoles) {
        final List<OrganisationResource> organisations = getApplicationOrganisations(applicationId);
        final List<OrganisationResource> academicOrganisations = getAcademicOrganisations(organisations);
        final List<Long> academicOrganisationIds = academicOrganisations.stream().map(ao -> ao.getId()).collect(Collectors.toList());
        model.addAttribute("academicOrganisations", academicOrganisations);
        model.addAttribute("applicationOrganisations", organisations);
        Map<Long, Boolean> applicantOrganisationsAreAcademic = organisations.stream().collect(Collectors.toMap(o -> o.getId(), o -> academicOrganisationIds.contains(o.getId())));
        model.addAttribute("applicantOrganisationIsAcademic", applicantOrganisationsAreAcademic);

        final List<String> activeApplicationOrganisationNames = simpleMap(organisations, OrganisationResource::getName);

        final List<String> pendingOrganisationNames = inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
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
        return simpleFilter(organisations, o -> OrganisationTypeEnum.RESEARCH.getId() == o.getOrganisationType());
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(final List<ProcessRoleResource> userApplicationRoles, List<OrganisationResource> organisations) {

        Optional<ProcessRoleResource> leadApplicantRole =
                simpleFindFirst(userApplicationRoles, uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()));

        return leadApplicantRole.flatMap(role -> simpleFindFirst(organisations, org -> org.getId().equals(role.getOrganisationId())));
    }
}
