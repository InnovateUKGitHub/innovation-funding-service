package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PendingPartnerProgressService {

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'READ_PENDING_PARTNER_PROGRESS')")
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

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markSubsidyBasisIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'UPDATE_PENDING_PARTNER_PROGRESS')")
    ServiceResult<Void> markSubsidyBasisComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @NotSecured(value = "This Service is to be used within other secured services")
    ServiceResult<Void> resetPendingPartnerProgress(QuestionSetupType subsidyBasis, long project, long organisation);
}
