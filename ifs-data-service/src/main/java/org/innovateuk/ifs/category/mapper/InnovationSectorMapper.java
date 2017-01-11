package org.innovateuk.ifs.category.mapper;

import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class,
        uses = { InnovationAreaMapper.class }
)
public abstract class InnovationSectorMapper extends BaseMapper<InnovationSector, InnovationSectorResource, Long> {


    @Mappings({
            @Mapping(target = "categoryLinks", ignore = true),
    })
    @Override
    public abstract InnovationSector mapToDomain(InnovationSectorResource resource);


    @Mappings({
            @Mapping(target = "parent", ignore = true),
    })
    @Override
    public abstract InnovationSectorResource mapToResource(InnovationSector innovationSector);

    public abstract List<InnovationSector> mapToDomain(List<InnovationSectorResource> innovationSectorResources);

    public abstract List<InnovationSectorResource> mapToResource(List<InnovationSector> innovationSectors);

    public Long mapInnovationAreaToId(InnovationSector object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public String mapCategoryToName(InnovationSector object) {
        return object.getName();
    }

}
