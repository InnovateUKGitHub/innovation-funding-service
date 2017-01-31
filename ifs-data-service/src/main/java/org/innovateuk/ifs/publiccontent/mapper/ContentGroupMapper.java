package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            FileEntryMapper.class
    }
)
public abstract class ContentGroupMapper extends BaseMapper<ContentGroup, ContentGroupResource, Long> {

    @Override
    @Mappings({
            @Mapping(source = "contentSection.id", target = "contentSectionId"),
            @Mapping(source = "contentSection.type", target = "sectionType")
    })
    public abstract ContentGroupResource mapToResource(ContentGroup domain);

    @Override
    @Mappings({
            @Mapping(target = "contentSection", ignore = true)
    })
    public abstract ContentGroup mapToDomain(ContentGroupResource resource);


    public Long mapPublicContentToId(ContentGroup object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
