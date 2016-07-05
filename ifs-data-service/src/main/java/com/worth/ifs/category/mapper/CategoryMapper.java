package com.worth.ifs.category.mapper;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
        }
)
public abstract class CategoryMapper extends BaseMapper<Category, CategoryResource, Long> {

    @Mapping(target = "categoryLinks", ignore = true)
    @Override
    public abstract Category mapToDomain(CategoryResource resource);

    public Long mapCategoryToId(Category object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}