package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface PartnerOrganisationService {
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId);

    @PostAuthorize("hasPermission(returnObject, 'VIEW_PARTNER_ORGANISATION')")
    ServiceResult<PartnerOrganisationResource> getPartnerOrganisation(Long projectId, Long organisationId);

    @Activity(projectOrganisationCompositeId = "projectOrganisationCompositeId", type = ActivityType.ORGANISATION_REMOVED)
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'REMOVE_PARTNER_ORGANISATION')")
    ServiceResult<Void> removePartnerOrganisation(ProjectOrganisationCompositeId projectOrganisationCompositeId);
}
