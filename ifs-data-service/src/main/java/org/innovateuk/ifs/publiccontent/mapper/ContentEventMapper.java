package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            PublicContentMapper.class
    }
)
public abstract class ContentEventMapper extends BaseMapper<ContentEvent, PublicContentEventResource, Long> {

    @Override
    public abstract PublicContentEventResource mapToResource(ContentEvent domain);

    @Mappings({
            @Mapping(target = "contentGroup", ignore = true)
    })
    public abstract ContentEvent mapToDomain(PublicContentEventResource domain);

    public Long mapContentEventToId(ContentEvent object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
