package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProcessRoleMapper.class,
                ApplicationFinanceMapper.class,
                CompetitionMapper.class,
                ApplicationInviteMapper.class,
                FileEntryMapper.class,
                ResearchCategoryMapper.class,
                InnovationAreaMapper.class
        }
)
public abstract class ApplicationMapper extends BaseMapper<Application, ApplicationResource, Long> {

    public Long mapApplicationToId(Application object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    @Mappings({
            @Mapping(source = "competition.name", target = "competitionName"),
            @Mapping(source = "competition.competitionStatus", target = "competitionStatus"),

    })
    @Override
    public abstract ApplicationResource mapToResource(Application domain);

    @Mappings({
            @Mapping(target = "fundingDecision", ignore = true),
            @Mapping(target = "formInputResponses", ignore = true),
            @Mapping(target = "invites", ignore = true),
            @Mapping(target = "applicationFinances", ignore = true),
            @Mapping(target = "processRoles", ignore = true),
            @Mapping(target = "manageFundingEmailDate", ignore = true)

    })
    @Override
    public abstract Application mapToDomain(ApplicationResource resource);
}
