package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeService {

    @NotSecured(value = "Public objects, just a collection of all different organisation types.", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationTypeResource> findOne(Long id);

    @NotSecured(value = "Public objects, just a collection of all different organisation types.", mustBeSecuredByOtherServices = false)
    ServiceResult<List<OrganisationTypeResource>> findAll();
}
