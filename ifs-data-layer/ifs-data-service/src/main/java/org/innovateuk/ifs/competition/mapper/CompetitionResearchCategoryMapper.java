package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionResearchCategoryLink;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionResearchCategoryMapper extends BaseMapper<CompetitionResearchCategoryLink, CompetitionResearchCategoryLinkResource, Long> {

    @Override
    public abstract CompetitionResearchCategoryLink mapToDomain(CompetitionResearchCategoryLinkResource resource);

    @Override
    public abstract CompetitionResearchCategoryLinkResource mapToResource(CompetitionResearchCategoryLink researchCategory);

    public abstract List<CompetitionResearchCategoryLinkResource> mapToDomain(List<CompetitionResearchCategoryLinkResource> researchCategoryResources);

    public abstract List<CompetitionResearchCategoryLinkResource> mapToResource(List<CompetitionResearchCategoryLink> researchCategories);

    public Long mapResearchCategoryToId(CompetitionResearchCategoryLinkResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }


}
