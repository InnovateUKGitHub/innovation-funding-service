package com.worth.ifs.user.security;

import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.OrganisationResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class OrganisationLookup {
    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationMapper organisationMapper;


    @PermissionEntityLookupStrategy
    public Organisation findOrganisationById(Long id){
        return organisationRepository.findOne(id);
    }

    @PermissionEntityLookupStrategy
    public OrganisationResource findRecourceById(Long id){
        return organisationMapper.mapToResource(organisationRepository.findOne(id));
    }
}
