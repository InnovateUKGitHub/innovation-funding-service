package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PendingPartnerProgressService {

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'READ')")
    ServiceResult<PendingPartnerProgressResource> getPendingPartnerProgress(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markYourOrganisationComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markYourFundingComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markTermsAndConditionsComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markYourOrganisationIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markYourFundingIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markTermsAndConditionsIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> completePartnerSetup(ProjectOrganisationCompositeId projectOrganisationCompositeId);

}
