package org.innovateuk.ifs.project.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                ApplicationMapper.class,
                ProjectUserMapper.class,
                FileEntryMapper.class
        }
)
public abstract class ProjectMapper extends BaseMapper<Project, ProjectResource, Long> {
    @Override
    public abstract ProjectResource mapToResource(Project project);

    @Mappings({
            @Mapping(target = "organisations", ignore = true),
            // TODO DW - for now, exclude partner organisations from mapper - include later though as will be really
            // useful
            @Mapping(target = "partnerOrganisations", ignore = true),
            @Mapping(target = "spendProfiles", ignore = true)
    })
    @Override
    public abstract Project mapToDomain(ProjectResource projectResource);


    public Long mapProjectToId(Project object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
