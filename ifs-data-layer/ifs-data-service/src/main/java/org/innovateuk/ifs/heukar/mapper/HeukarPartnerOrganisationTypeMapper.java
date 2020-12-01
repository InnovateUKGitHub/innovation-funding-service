package org.innovateuk.ifs.heukar.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HeukarPartnerOrganisationTypeMapper extends BaseMapper<HeukarPartnerOrganisation, HeukarPartnerOrganisationResource, Long> {

    @Autowired
    private OrganisationTypeMapper organisationTypeMapper;

    @Override
    public HeukarPartnerOrganisationResource mapToResource(HeukarPartnerOrganisation domain) {
        HeukarPartnerOrganisationResource resource = new HeukarPartnerOrganisationResource();
        resource.setApplicationId(domain.getApplicationId());
        resource.setId(domain.getId());
        resource.setOrganisationTypeResource(organisationTypeMapper.mapToResource(domain.getOrganisationType()));
        return resource;
    }

    @Override
    public Iterable<HeukarPartnerOrganisationResource> mapToResource(Iterable<HeukarPartnerOrganisation> domain) {
        return null;
    }

    @Override
    public HeukarPartnerOrganisation mapToDomain(HeukarPartnerOrganisationResource resource) {
        return mapIdsToDomain(resource.getApplicationId(), resource.getOrganisationTypeResource().getId());
    }

    public HeukarPartnerOrganisation mapIdsToDomain(Long applicationId, Long organisationTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setOrganisationType(organisationTypeMapper.mapIdToDomain(organisationTypeId));
        domain.setApplicationId(applicationId);
        return domain;
    }

    @Override
    public Iterable<HeukarPartnerOrganisation> mapToDomain(Iterable<HeukarPartnerOrganisationResource> resource) {
        return null;
    }
}
