package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
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
