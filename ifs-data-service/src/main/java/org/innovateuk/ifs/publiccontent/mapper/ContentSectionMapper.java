package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            PublicContentMapper.class,
            ContentGroupMapper.class
    }
)
public abstract class ContentSectionMapper extends BaseMapper<ContentSection, PublicContentSectionResource, Long> {

    @Override
    public abstract PublicContentSectionResource mapToResource(ContentSection domain);


    @Mappings({
            @Mapping(target = "contentGroups", ignore = true)
    })
    public abstract ContentSection mapToDomain(PublicContentSectionResource domain);

    public Long mapContentSectionToId(ContentSection object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
