package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.threads.domain.ProjectStateComments;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                PostMapper.class,
                UserMapper.class
        }
)
public abstract class ProjectStateCommentsMapper extends BaseMapper<ProjectStateComments, ProjectStateCommentsResource, Long> {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ProjectStateCommentsResource mapToResource(ProjectStateComments query) {
        return new ProjectStateCommentsResource(query.id(), query.contextClassPk(), simpleMap(query.posts(), postMapper::mapToResource), query.getProjectState(),
                query.title(), query.createdOn(),
                query.getClosedBy() != null ? userMapper.mapToResource(query.getClosedBy()) : null,
                query.getClosedDate());
    }

    @Override
    public ProjectStateComments mapToDomain(ProjectStateCommentsResource queryResource) {
        ProjectStateComments query = new ProjectStateComments(queryResource.id, queryResource.contextClassPk, simpleMap(queryResource.posts, postMapper::mapToDomain),
                queryResource.title, queryResource.state,  queryResource.createdOn);

        if (queryResource.closedBy != null) {
            query.setClosedBy(userMapper.mapToDomain(queryResource.closedBy));
        }

        query.setClosedDate(queryResource.closedDate);
        return query;
    }

}
