package com.worth.ifs.project.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.mapper.PartnerOrganisationMapper;
import com.worth.ifs.project.repository.PartnerOrganisationRepository;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class PartnerOrganisationLookupStrategy {

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private PartnerOrganisationMapper partnerOrganisationMapper;

    @PermissionEntityLookupStrategy
    public PartnerOrganisation getPartnerOrganisation(Long partnerOrganisationId) {
        return partnerOrganisationRepository.findOne(partnerOrganisationId);
    }

    @PermissionEntityLookupStrategy
    public PartnerOrganisationResource getPartnerOrganisationResource(Long partnerOrganisationId) {
        return partnerOrganisationMapper.mapToResource(partnerOrganisationRepository.findOne(partnerOrganisationId));
    }
}
