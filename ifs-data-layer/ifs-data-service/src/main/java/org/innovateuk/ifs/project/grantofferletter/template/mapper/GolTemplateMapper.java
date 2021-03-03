package org.innovateuk.ifs.project.grantofferletter.template.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.grantofferletter.template.domain.GolTemplate;
import org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config = GlobalMapperConfig.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class GolTemplateMapper
        extends BaseMapper<GolTemplate, GolTemplateResource, Long> {

    @Mappings({
            @Mapping(source = "createdBy.name", target = "createdBy"),
            @Mapping(source = "modifiedBy.name", target = "modifiedBy")})
    @Override
    public abstract GolTemplateResource mapToResource(GolTemplate domain);

}
