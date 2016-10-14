package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

public interface PartnerOrganisationService {
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId);
}
