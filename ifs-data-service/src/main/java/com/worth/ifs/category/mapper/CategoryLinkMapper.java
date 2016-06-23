package com.worth.ifs.category.mapper;

import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryLinkResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CategoryMapper.class
        }
)
public abstract class CategoryLinkMapper extends BaseMapper<CategoryLink, CategoryLinkResource, Long> {

    public Long mapCategoryLinkToId(CategoryLink object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}