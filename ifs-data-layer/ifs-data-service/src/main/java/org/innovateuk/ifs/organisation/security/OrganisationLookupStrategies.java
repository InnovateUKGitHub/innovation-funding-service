package org.innovateuk.ifs.organisation.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.user.resource.OrganisationResource;

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
