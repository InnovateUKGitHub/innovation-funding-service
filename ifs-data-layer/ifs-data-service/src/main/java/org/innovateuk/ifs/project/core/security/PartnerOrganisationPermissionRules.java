package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

@PermissionRules
@Component
public class PartnerOrganisationPermissionRules extends BasePermissionRules {
    @PermissionRule(value = "READ", description = "A partner can see a list of all partner organisations on their project")
    public boolean partnersOnProjectCanView(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return isPartner(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Internal users can see partner organisations for any project")
    public boolean internalUsersCanView(PartnerOrganisationResource partnerOrganisation, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Stakeholders can see partner organisations on projects in competitions they are assigned to")
    public boolean stakeholdersCanViewProjects(PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return userIsStakeholderOnCompetitionForProject(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Competition finances users can see partner organisations on projects in competitions they are assigned to")
    public boolean competitionFinanceUsersCanViewProjects(PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Monitoring officers can see partner organisations on a project they are assigned to")
    public boolean monitoringOfficersUsersCanView(PartnerOrganisationResource partnerOrganisation, UserResource user) {
        return isMonitoringOfficer(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "READ_PENDING_PARTNER_PROGRESS", description = "Partners can read their own progress when setting up their organisation")
    public boolean partnersCanReadTheirOwnPendingPartnerProgress(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return partnerBelongsToOrganisation(partnerOrganisation.getProject(), user.getId(), partnerOrganisation.getOrganisation());
    }

    @PermissionRule(value = "READ_PENDING_PARTNER_PROGRESS", description = "Internal users can read partner progress in project setup")
    public boolean internalUsersCanReadPendingPartnerProgress(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_PENDING_PARTNER_PROGRESS", description = "Stakeholders can read partner progress in project setup in competitions they are assigned to")
    public boolean stakeholdersCanReadPendingPartnerProgress(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return userIsStakeholderOnCompetitionForProject(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "READ_PENDING_PARTNER_PROGRESS", description = "Competition finance users can read partner progress in project setup in competitions they are assigned to")
    public boolean competitionFinanceUsersCanReadPendingPartnerProgress(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "UPDATE_PENDING_PARTNER_PROGRESS", description = "Partners can update their own progress when setting up their organisation")
    public boolean partnersCanUpdateTheirOwnPendingPartnerProgress(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return partnerBelongsToOrganisation(partnerOrganisation.getProject(), user.getId(), partnerOrganisation.getOrganisation());
    }

    @PermissionRule(value = "REMOVE_PARTNER_ORGANISATION", description = "Internal users can remove partner organisations for any project")
    public boolean internalUsersCanRemovePartnerOrganisations(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return isInternalAdmin(user);
    }
}
