package org.innovateuk.ifs.heukar.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class HeukarPartnerOrganisationMapper extends BaseMapper<HeukarPartnerOrganisation, HeukarPartnerOrganisationResource, Long> {

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
        return (Iterable<HeukarPartnerOrganisationResource>) Stream.of(domain).map(this::mapToResource);
    }

    public HeukarPartnerOrganisation mapExistingToDomain(Long partnerOrgId, Long applicationId, Long orgTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setApplicationId(applicationId);
        domain.setOrganisationType(organisationTypeMapper.mapIdToDomain(orgTypeId));
        domain.setId(partnerOrgId);
        return domain;
    }

    @Override
    public HeukarPartnerOrganisation mapToDomain(HeukarPartnerOrganisationResource resource) {
        return mapWithApplicationIdToDomain(resource.getApplicationId(), resource.getOrganisationTypeResource().getId());
    }

    public HeukarPartnerOrganisation mapWithApplicationIdToDomain(Long applicationId, Long organisationTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setOrganisationType(organisationTypeMapper.mapIdToDomain(organisationTypeId));
        domain.setApplicationId(applicationId);
        return domain;
    }

    @Override
    public Iterable<HeukarPartnerOrganisation> mapToDomain(Iterable<HeukarPartnerOrganisationResource> resource) {
        return (Iterable<HeukarPartnerOrganisation>) Stream.of(resource).map(this::mapToDomain);
    }
}
