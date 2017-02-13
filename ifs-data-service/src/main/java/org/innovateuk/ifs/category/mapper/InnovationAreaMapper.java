package org.innovateuk.ifs.category.mapper;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

@Mapper(config = GlobalMapperConfig.class,
        uses = { InnovationSectorMapper.class })
public abstract class InnovationAreaMapper extends BaseMapper<InnovationArea, InnovationAreaResource, Long> {

    @Override
    public abstract InnovationArea mapToDomain(InnovationAreaResource resource);

    @Mappings({
            @Mapping(source = "sector.name", target = "sectorName"),
    })
    @Override
    public abstract InnovationAreaResource mapToResource(InnovationArea innovationArea);

    public abstract List<InnovationArea> mapToDomain(List<InnovationAreaResource> innovationAreaResourcesResources);

    public abstract List<InnovationAreaResource> mapToResource(List<InnovationArea> innovationAreas);

    public Long mapInnovationAreaToId(InnovationArea object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Set<String> mapInnovationAreaNames(Set<InnovationArea> innovationAreas) {
        if (innovationAreas == null) {
            return Collections.emptySet();
        }

        return simpleMapSet(innovationAreas, Category::getName);
    }

    public Long mapCategoryToId(Category object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public String mapCategoryToName(InnovationArea object) {
        return object.getName();
    }
}
