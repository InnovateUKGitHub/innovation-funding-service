package com.worth.ifs.project.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                ProcessRoleMapper.class,
                ApplicationMapper.class
        }
)
public abstract class ProjectMapper extends BaseMapper<Project, ProjectResource, Long> {
    @Override
    public abstract ProjectResource mapToResource(Project project);

    @Override
    public abstract Project mapToDomain(ProjectResource projectResource);
}