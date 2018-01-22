package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.resource.QueryResource;
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
public abstract class QueryMapper extends BaseMapper<Query, QueryResource, Long> {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public QueryResource mapToResource(Query query) {
        return new QueryResource(query.id(), query.contextClassPk(), simpleMap(query.posts(), postMapper::mapToResource), query.section(),
                query.title(), query.isAwaitingResponse(), query.createdOn(),
                query.getClosedBy() != null ? userMapper.mapToResource(query.getClosedBy()) : null,
                query.getClosedDate());
    }

    @Override
    public Query mapToDomain(QueryResource queryResource) {
        Query query = new Query(queryResource.id, queryResource.contextClassPk, simpleMap(queryResource.posts, postMapper::mapToDomain),
                queryResource.section, queryResource.title, queryResource.createdOn);

        if (queryResource.closedBy != null) {
            query.setClosedBy(userMapper.mapToDomain(queryResource.closedBy));
        }

        query.setClosedDate(queryResource.closedDate);
        return query;
    }

}
