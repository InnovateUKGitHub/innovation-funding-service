package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.QueryResource;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectFinanceQueriesService extends ThreadService<QueryResource, PostResource> {
    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QueryResource>> findAll(Long contextClassPk);

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<QueryResource> findOne(Long contextClassPk);

    @Override
    @PreAuthorize("hasPermission(#QueryResource, 'CREATE')")
    ServiceResult<Void> create(QueryResource QueryResource);

    @Override
    @PreAuthorize("hasPermission(#querId, 'org.innovateuk.threads.resource.QueryResource', 'ADD_POST')")
    ServiceResult<Void> addPost(PostResource post, Long queryId);
}
