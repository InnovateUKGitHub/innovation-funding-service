package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            ContentSectionMapper.class,
            KeywordMapper.class,
            CompetitionMapper.class
    }
)
public abstract class PublicContentMapper extends BaseMapper<PublicContent, PublicContentResource, Long> {

    @Override
    @Mappings({
            @Mapping(target = "competitionId", ignore = true)
    })
    public abstract PublicContentResource mapToResource(PublicContent domain);

    @Mappings({
            @Mapping(target = "contentEvents", ignore = true),
            @Mapping(target = "competition", ignore = true)
    })
    public abstract PublicContent mapToDomain(PublicContentResource domain);

    public Long mapPublicContentToId(PublicContent object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
