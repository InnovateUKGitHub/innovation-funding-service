package org.innovateuk.ifs.project.queries.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FinanceCheckQueriesService extends ThreadService<QueryResource, PostResource> {
    @Override
    @PostFilter("hasPermission(filterObject, 'PF_READ')")
    ServiceResult<List<QueryResource>> findAll(Long contextClassPk);

    @Override
    @PostAuthorize("hasPermission(returnObject, 'PF_READ')")
    ServiceResult<QueryResource> findOne(Long id);

    @Override
    @PreAuthorize("hasPermission(#queryResource, 'PF_CREATE')")
    ServiceResult<Long> create(@P("queryResource") QueryResource queryResource);

    @Override
    @PreAuthorize("hasPermission(#queryId, 'org.innovateuk.threads.resource.QueryResource', 'PF_ADD_POST')")
    ServiceResult<Void> addPost(PostResource post, @P("queryId") final Long queryId);
}