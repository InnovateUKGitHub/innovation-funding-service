package org.innovateuk.ifs.heukar.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum.fromId;

@Component
public class HeukarPartnerOrganisationMapper extends BaseMapper<HeukarPartnerOrganisation, HeukarPartnerOrganisationResource, Long> {

    @Override
    public HeukarPartnerOrganisationResource mapToResource(HeukarPartnerOrganisation domain) {
        HeukarPartnerOrganisationResource resource = new HeukarPartnerOrganisationResource();
        resource.setApplicationId(domain.getApplicationId());
        resource.setId(domain.getId());
        resource.setOrganisationTypeId(domain.getOrganisationType().getId());
        resource.setName(domain.getOrganisationType().getName());
        resource.setDescription(domain.getOrganisationType().getDescription());
        return resource;
    }

    @Override
    public Iterable<HeukarPartnerOrganisationResource> mapToResource(Iterable<HeukarPartnerOrganisation> domain) {
        return (Iterable<HeukarPartnerOrganisationResource>) Stream.of(domain).map(this::mapToResource);
    }

    public HeukarPartnerOrganisation mapExistingToDomain(Long partnerOrgId, Long applicationId, Long orgTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setOrganisationType(fromId(orgTypeId));
        domain.setApplicationId(applicationId);
        domain.setId(partnerOrgId);
        return domain;
    }

    @Override
    public HeukarPartnerOrganisation mapToDomain(HeukarPartnerOrganisationResource resource) {
        return mapWithApplicationIdToDomain(resource.getApplicationId(), resource.getOrganisationTypeId());
    }

    public HeukarPartnerOrganisation mapWithApplicationIdToDomain(Long applicationId, Long organisationTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setOrganisationType(fromId(organisationTypeId));
        domain.setApplicationId(applicationId);
        return domain;
    }

    @Override
    public Iterable<HeukarPartnerOrganisation> mapToDomain(Iterable<HeukarPartnerOrganisationResource> resource) {
        return (Iterable<HeukarPartnerOrganisation>) Stream.of(resource).map(this::mapToDomain);
    }
}
