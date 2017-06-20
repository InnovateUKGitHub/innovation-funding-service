package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class QueryLookupStrategy {

    @Autowired
    private QueryRepository repository;

    @Autowired
    private QueryMapper mapper;

    @PermissionEntityLookupStrategy
    public QueryResource findById(final Long queryId) {
        return mapper.mapToResource(repository.findOne(queryId));
    }

}