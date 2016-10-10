package com.worth.ifs.project.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.finance.mapper.SpendProfileMapper;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.file.mapper.FileEntryMapper;
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