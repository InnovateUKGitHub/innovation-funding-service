package org.innovateuk.ifs.competitionsetup.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competitionsetup.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class SiteTermsAndConditionsMapper
        extends BaseMapper<SiteTermsAndConditions, SiteTermsAndConditionsResource, Long> {

    @Mappings({
            @Mapping(source = "createdBy.name", target = "createdBy"),
            @Mapping(source = "modifiedBy.name", target = "modifiedBy")})
    @Override
    public abstract SiteTermsAndConditionsResource mapToResource(SiteTermsAndConditions domain);

}
