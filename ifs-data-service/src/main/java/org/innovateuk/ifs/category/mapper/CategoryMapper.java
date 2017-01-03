package org.innovateuk.ifs.category.mapper;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CategoryMapper extends BaseMapper<Category, CategoryResource, Long> {

    @Mapping(target = "categoryLinks", ignore = true)
    @Override
    public abstract Category mapToDomain(CategoryResource resource);

    public abstract List<Category> mapToDomain(List<CategoryResource> categoryResources);

    public abstract List<CategoryResource> mapToResource(List<Category> categories);

    public Long mapCategoryToId(Category object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public String mapCategoryToName(Category object) {
        return object.getName();
    }
}
