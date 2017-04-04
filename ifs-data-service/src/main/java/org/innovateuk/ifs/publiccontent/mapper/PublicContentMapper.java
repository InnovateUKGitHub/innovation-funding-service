package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            ContentEventMapper.class,
            ContentSectionMapper.class,
            KeywordMapper.class
    }
)
public abstract class PublicContentMapper extends BaseMapper<PublicContent, PublicContentResource, Long> {

    @Override
    public abstract PublicContentResource mapToResource(PublicContent domain);

    public abstract PublicContent mapToDomain(PublicContentResource domain);

    public Long mapPublicContentToId(PublicContent object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
