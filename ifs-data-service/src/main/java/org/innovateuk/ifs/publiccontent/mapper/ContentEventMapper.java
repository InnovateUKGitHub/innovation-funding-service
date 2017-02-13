package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            PublicContentMapper.class
    }
)
public abstract class ContentEventMapper extends BaseMapper<ContentEvent, ContentEventResource, Long> {

    @Override
    public abstract ContentEventResource mapToResource(ContentEvent domain);

    public abstract ContentEvent mapToDomain(ContentEventResource resource);

    public Long mapContentEventToId(ContentEvent object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
