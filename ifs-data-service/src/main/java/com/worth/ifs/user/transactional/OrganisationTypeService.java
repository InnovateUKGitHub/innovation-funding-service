package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeService {

    @NotSecured("Public objects, just a collection of all different organisation types.")
    ServiceResult<OrganisationTypeResource> findOne(Long id);

    @NotSecured("Public objects, just a collection of all different organisation types.")
    ServiceResult<List<OrganisationTypeResource>> findAll();
}