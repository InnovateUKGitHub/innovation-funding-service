package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Thread;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.repository.ThreadRepository;
import org.innovateuk.ifs.threads.service.GenericThreadService;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class MappingThreadService<D extends Thread, R, M extends BaseMapper<D, R, Long>, C> implements ThreadService<R, PostResource> {
    private final GenericThreadService<D, C> service;
    private final M queryMapper;
    private final PostMapper postMapper;

    public MappingThreadService(ThreadRepository<D> threadRepository, M queryMapper, PostMapper postMapper, Class<C> context) {
        this.service = new GenericThreadService(threadRepository, context);
        this.queryMapper = queryMapper;
        this.postMapper = postMapper;
    }

    public final ServiceResult<List<R>> findAll(Long projectFinanceId) {
        return service.findAll(projectFinanceId)
                .andOnSuccessReturn(queries -> simpleMap(queries, queryMapper::mapToResource));
    }

    public final ServiceResult<R> findOne(Long contextClassPk) {
        return service.findOne(contextClassPk).andOnSuccessReturn(queryMapper::mapToResource);
    }

    public final ServiceResult<Void> create(R query) {
        return service.create(queryMapper.mapToDomain(query));
    }

    public final ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(postMapper.mapToDomain(post), threadId);
    }
}
