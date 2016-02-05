package com.worth.ifs.user.service;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeRestService {
    @NotSecured("Public data, nothing special, used while registering a new account.")
    OrganisationTypeResource findOne(Long id);
    @NotSecured("Public data, nothing special, used while registering a new account.")
    List<OrganisationTypeResource> getAll();
}