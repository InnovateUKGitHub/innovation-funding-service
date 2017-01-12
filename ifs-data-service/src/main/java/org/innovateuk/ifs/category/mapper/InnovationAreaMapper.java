package org.innovateuk.ifs.category.mapper;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class,
        uses = { InnovationSectorMapper.class })
public abstract class InnovationAreaMapper extends BaseMapper<InnovationArea, InnovationAreaResource, Long> {


    @Override
    public abstract InnovationArea mapToDomain(InnovationAreaResource resource);

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
