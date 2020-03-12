package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.mapper.CompetitionDocumentMapper;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.*;

import java.util.stream.Collectors;

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
                GrantTermsAndConditionsMapper.class,
                GrantClaimMaximumMapper.class,
                CompetitionDocumentMapper.class,
                FileTypeMapper.class,
                FileEntryMapper.class
        })
public abstract class CompetitionMapper extends BaseMapper<Competition, CompetitionResource, Long> {

    @Mappings({
            @Mapping(source = "innovationAreas", target = "innovationAreaNames"),
            @Mapping(source = "innovationSector.name", target = "innovationSectorName"),
            @Mapping(source = "competitionType.name", target = "competitionTypeName"),
            @Mapping(source = "leadTechnologist.name", target = "leadTechnologistName"),
            @Mapping(source = "executive.name", target = "executiveName"),
            @Mapping(source = "createdBy.name", target = "createdBy"),
            @Mapping(source = "modifiedBy.name", target = "modifiedBy"),
            @Mapping(source = "competitionOrganisationConfig.internationalOrganisationsAllowed", target = "internationalOrganisationsAllowed")
    })
    @Override
    public abstract CompetitionResource mapToResource(Competition domain);

    @Mappings({
            @Mapping(target = "sections", ignore = true),
            @Mapping(target = "questions", ignore = true),
            @Mapping(target = "template", ignore = true),
            @Mapping(target = "assessmentPanelDate", ignore = true),
            @Mapping(target = "panelDate", ignore = true),
            @Mapping(target = "projectStages", ignore = true),
            @Mapping(target = "competitionOrganisationConfig", ignore = true)
    })
    public abstract Competition mapToDomain(CompetitionResource domain);

    public Long mapCompetitionToId(Competition object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    @AfterMapping
    public void setStagesOnDomain(@MappingTarget Competition competition, CompetitionResource resource) {
        competition.setProjectStages(
            resource.getProjectSetupStages()
                .stream()
                .map(stage -> mapProjectSetupStageToProjectStage(stage, competition))
                .collect(Collectors.toList())
        );
    }

    @AfterMapping
    public void setInternationalOrganisationsAllowed(@MappingTarget Competition competition, CompetitionResource resource) {

        boolean internationalOrgsAllowed = resource.getInternationalOrganisationsAllowed() != null ? resource.getInternationalOrganisationsAllowed() : false;
        competition.setCompetitionOrganisationConfig(new CompetitionOrganisationConfig(competition, internationalOrgsAllowed));
    }

    private ProjectStages mapProjectSetupStageToProjectStage(ProjectSetupStage projectSetupStage, Competition competition) {
        return new ProjectStages(competition, projectSetupStage);
    }

}
