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
            ContentSectionMapper.class,
            FileEntryMapper.class
    }
)
public abstract class ContentGroupMapper extends BaseMapper<ContentGroup, ContentGroupResource, Long> {

    @Override
    public abstract ContentGroupResource mapToResource(ContentGroup domain);

    @Mappings({
            @Mapping(target = "contentSectionId", ignore = true)
    })
    public abstract ContentGroup mapToDomain(ContentGroupResource domain);

    public Long mapPublicContentToId(ContentGroup object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
