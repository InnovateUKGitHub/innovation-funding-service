package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

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

    @PermissionRule(value = "READ", description = "Monitoring officers can see partner organisations on a project they are assigned to")
    public boolean monitoringOfficersUsersCanView(PartnerOrganisationResource partnerOrganisation, UserResource user) {
        return isMonitoringOfficer(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "VIEW_PARTNER_ORGANISATION", description = "Internal users can see partner organisations for any project")
    public boolean internalUsersCanViewPartnerOrganisations(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "VIEW_PARTNER_ORGANISATION", description = "Partners can view their own partner organisation")
    public boolean partnersCanViewTheirOwnPartnerOrganisation(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return partnerBelongsToOrganisation(partnerOrganisation.getProject(), user.getId(), partnerOrganisation.getOrganisation());
    }

    @PermissionRule(value = "PENDING_PARTNER_PROGRESS", description = "Partners can view their own progress setting up their organisation")
    public boolean partnersCanViewTheirOwnPendingPartnerProgress(final PartnerOrganisationResource partnerOrganisation, final UserResource user) {
        return partnerBelongsToOrganisation(partnerOrganisation.getProject(), user.getId(), partnerOrganisation.getOrganisation());
    }
}
