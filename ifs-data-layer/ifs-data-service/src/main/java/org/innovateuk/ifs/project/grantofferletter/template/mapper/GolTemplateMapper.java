package org.innovateuk.ifs.project.grantofferletter.template.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.grantofferletter.template.domain.GolTemplate;
import org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class GolTemplateMapper
        extends BaseResourceMapper<GolTemplate, GolTemplateResource> {

    @Mappings({
            @Mapping(source = "createdBy.name", target = "createdBy"),
            @Mapping(source = "modifiedBy.name", target = "modifiedBy")})
    @Override
    public abstract GolTemplateResource mapToResource(GolTemplate domain);

}
