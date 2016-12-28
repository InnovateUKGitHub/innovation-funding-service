package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.application.mapper.SectionMapper;
import org.innovateuk.ifs.category.mapper.CategoryLinkMapper;
import org.innovateuk.ifs.category.mapper.CategoryMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ApplicationMapper.class,
        QuestionMapper.class,
        UserMapper.class,
        CategoryMapper.class,
        CategoryLinkMapper.class,
        MilestoneMapper.class,
        CompetitionTypeMapper.class,
        SectionMapper.class,
        CompetitionFunderMapper.class
    }
)
public abstract class CompetitionMapper extends BaseMapper<Competition, CompetitionResource, Long> {

    @Mappings({
            @Mapping(source = "innovationArea.name", target = "innovationAreaName"),
            @Mapping(source = "innovationSector.name", target = "innovationSectorName"),
            @Mapping(source = "competitionType.name", target = "competitionTypeName"),
            @Mapping(source = "leadTechnologist.name", target = "leadTechnologistName")
    })
    @Override
    public abstract CompetitionResource mapToResource(Competition domain);

    @Mappings({
            @Mapping(target = "sections", ignore = true),
            @Mapping(target = "questions", ignore = true),
            @Mapping(target = "template", ignore = true),
            @Mapping(target = "applications", ignore = true)
    })
    public abstract Competition mapToDomain(CompetitionResource domain);

    public Long mapCompetitionToId(Competition object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
