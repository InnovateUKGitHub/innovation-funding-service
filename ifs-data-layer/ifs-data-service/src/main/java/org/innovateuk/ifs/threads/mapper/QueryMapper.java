package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.threads.resource.QueryResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {PostMapper.class}
)
public abstract class QueryMapper extends BaseMapper<Query, QueryResource, Long> {
    @Autowired
    private PostMapper postMapper;

    @Override
    public QueryResource mapToResource(Query query) {
        return new QueryResource(query.id(), query.contextClassPk(), simpleMap(query.posts(), postMapper::mapToResource), query.section(),
                    query.title(), query.isAwaitingResponse(), query.createdOn());
    }


    @Override
    public Query mapToDomain(QueryResource queryResource) {
        return new Query(queryResource.id, queryResource.contextClassPk, simpleMap(queryResource.posts, postMapper::mapToDomain),
                queryResource.section, queryResource.title, queryResource.createdOn);
    }

}
