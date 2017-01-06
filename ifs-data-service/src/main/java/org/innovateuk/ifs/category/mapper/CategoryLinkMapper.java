package org.innovateuk.ifs.category.mapper;

import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.resource.CategoryLinkResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CategoryMapper.class
        }
)
public abstract class CategoryLinkMapper extends BaseMapper<CompetitionCategoryLink, CategoryLinkResource, Long> {

    public Long mapCategoryLinkToId(CompetitionCategoryLink object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
