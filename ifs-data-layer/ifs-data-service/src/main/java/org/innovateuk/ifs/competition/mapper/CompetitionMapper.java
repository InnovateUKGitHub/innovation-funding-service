package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.user.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                QuestionMapper.class,
                UserMapper.class,
                InnovationAreaMapper.class,
                InnovationSectorMapper.class,
                ResearchCategoryMapper.class,
                MilestoneMapper.class,
                CompetitionTypeMapper.class,
                SectionMapper.class,
                CompetitionFunderMapper.class,
                OrganisationTypeMapper.class,
                TermsAndConditionsMapper.class
        }
)
public abstract class CompetitionMapper extends BaseMapper<Competition, CompetitionResource, Long> {

    @Mappings({
            @Mapping(source = "innovationAreas", target = "innovationAreaNames"),
            @Mapping(source = "innovationSector.name", target = "innovationSectorName"),
            @Mapping(source = "competitionType.name", target = "competitionTypeName"),
            @Mapping(source = "leadTechnologist.name", target = "leadTechnologistName"),
            @Mapping(source = "executive.name", target = "executiveName"),
    })
    @Override
    public abstract CompetitionResource mapToResource(Competition domain);


    /*
     * TODO: ZDD contract cleanup up for IFS-2776 as part of IFS-3186.
     * The web-service might send an old CompetitionResource without the min/max project duration fields.
     * These values would be set onto the domain object as null values. To prevent erroneously saving these values
     * we'll assume an empty min/max field should have contained the default values.
     */
    @ZeroDowntime(
            reference = "IFS-2776",
            description = "@Mapping with defaults for max / min project duration are necessary during migration phase, and can be removed" +
                    " in contract deploy."
    )
    @Mappings({
            @Mapping(target = "sections", ignore = true),
            @Mapping(target = "questions", ignore = true),
            @Mapping(target = "template", ignore = true),
            @Mapping(target = "assessmentPanelDate", ignore = true),
            @Mapping(target = "panelDate", ignore = true),
            @Mapping(target = "maxProjectDuration", defaultValue = "36"),
            @Mapping(target = "minProjectDuration", defaultValue = "1")
    })
    public abstract Competition mapToDomain(CompetitionResource domain);

    public Long mapCompetitionToId(Competition object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
