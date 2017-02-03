package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.QueryMapper;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.GenericThreadService;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectFinanceQueriesServiceImpl implements ProjectFinanceQueriesService {
    private final GenericThreadService<Query, Post, ProjectFinance> service;
    private final QueryMapper queryMapper;
    private final PostMapper postMapper;

    @Autowired
    public ProjectFinanceQueriesServiceImpl(QueryRepository queryRepository, QueryMapper queryMapper, PostMapper postMapper) {
        this.service = new GenericThreadService<>(queryRepository, ProjectFinance.class);
        this.queryMapper = queryMapper;
        this.postMapper = postMapper;
    }

    public final ServiceResult<List<QueryResource>> findAll(Long projectFinanceId) {
        return service.findAll(projectFinanceId)
                .andOnSuccessReturn(queries -> simpleMap(queries, queryMapper::mapToResource));
    }

    public final ServiceResult<QueryResource> findOne(Long contextClassPk) {
        return service.findOne(contextClassPk).andOnSuccessReturn(queryMapper::mapToResource);
    }

    public final ServiceResult<Void> create(QueryResource query) {
        return service.create(queryMapper.mapToDomain(query));
    }

    public final ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(postMapper.mapToDomain(post), threadId);
    }

}