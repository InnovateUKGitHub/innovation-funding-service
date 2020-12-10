package org.innovateuk.ifs.heukar.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import static org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum.fromId;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class HeukarPartnerOrganisationMapper extends BaseMapper<HeukarPartnerOrganisation, HeukarPartnerOrganisationResource, Long> {

    @Mappings({
            @Mapping(source = "organisationType", target = "heukarPartnerOrganisationType"),
    })
    @Override
    public abstract HeukarPartnerOrganisationResource mapToResource(HeukarPartnerOrganisation domain);


    @Mappings({
            @Mapping(source = "heukarPartnerOrganisationType", target = "organisationType")
    })
    @Override
    public abstract HeukarPartnerOrganisation mapToDomain(HeukarPartnerOrganisationResource resource);

    public HeukarPartnerOrganisation mapWithApplicationIdToDomain(Long applicationId, Long organisationTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setOrganisationType(fromId(organisationTypeId));
        domain.setApplicationId(applicationId);
        return domain;
    }

    public HeukarPartnerOrganisation mapExistingToDomain(Long partnerOrgId, Long applicationId, Long orgTypeId) {
        HeukarPartnerOrganisation domain = new HeukarPartnerOrganisation();
        domain.setOrganisationType(fromId(orgTypeId));
        domain.setApplicationId(applicationId);
        domain.setId(partnerOrgId);
        return domain;
    }

}
