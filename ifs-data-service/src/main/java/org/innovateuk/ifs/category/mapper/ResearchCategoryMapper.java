package org.innovateuk.ifs.category.mapper;

import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ResearchCategoryMapper extends BaseMapper<ResearchCategory, ResearchCategoryResource, Long> {


    @Mappings({
            @Mapping(target = "categoryLinks", ignore = true),
    })
    @Override
    public abstract ResearchCategory mapToDomain(ResearchCategoryResource resource);


    @Mappings({
            @Mapping(target = "parent", ignore = true),
    })
    @Override
    public abstract ResearchCategoryResource mapToResource(ResearchCategory researchCategory);

    public abstract List<ResearchCategory> mapToDomain(List<ResearchCategoryResource> researchCategoryResources);

    public abstract List<ResearchCategoryResource> mapToResource(List<ResearchCategory> researchCategories);

    public Long mapInnovationAreaToId(ResearchCategory object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public String mapCategoryToName(ResearchCategory object) {
        return object.getName();
    }

}
