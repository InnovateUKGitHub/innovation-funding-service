package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface PartnerOrganisationService {
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<PartnerOrganisationResource> getPartnerOrganisation(Long projectId, Long organisationId);

    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.ORGANISATION_REMOVED)
    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'REMOVE_PARTNER_ORGANISATION')")
    ServiceResult<Void> removePartnerOrganisation(ProjectOrganisationCompositeId projectOrganisationCompositeId);
}
