package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeService {

    @NotSecured(value = "Public objects, just a collection of all different organisation types.", mustBeSecuredByOtherServices = false)
    ServiceResult<OrganisationTypeResource> findOne(Long id);

    @NotSecured(value = "Public objects, just a collection of all different organisation types.", mustBeSecuredByOtherServices = false)
    ServiceResult<List<OrganisationTypeResource>> findAll();
}