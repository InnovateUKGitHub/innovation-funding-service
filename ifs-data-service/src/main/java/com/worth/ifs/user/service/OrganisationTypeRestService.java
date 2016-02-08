package com.worth.ifs.user.service;

import com.worth.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeRestService {
    OrganisationTypeResource findOne(Long id);
    List<OrganisationTypeResource> getAll();
}