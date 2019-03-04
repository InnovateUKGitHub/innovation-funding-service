package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
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
        return partnerOrganisationRepository.findById(partnerOrganisationId).orElse(null);
    }

    @PermissionEntityLookupStrategy
    public PartnerOrganisationResource getPartnerOrganisationResource(Long partnerOrganisationId) {
        return partnerOrganisationMapper.mapToResource(partnerOrganisationRepository.findById(partnerOrganisationId).orElse(null));
    }
}
