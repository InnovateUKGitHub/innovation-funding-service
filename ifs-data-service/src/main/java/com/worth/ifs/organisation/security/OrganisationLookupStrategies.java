package com.worth.ifs.organisation.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.user.resource.OrganisationResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class OrganisationLookupStrategies {

    @Autowired
    private OrganisationMapper organisationMapper;

    @PermissionEntityLookupStrategy
    public OrganisationResource findOrganisationById(Long id){
        return organisationMapper.mapIdToResource(id);
    }
}
