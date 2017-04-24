package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Thread;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.repository.ThreadRepository;
import org.innovateuk.threads.resource.PostResource;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class MappingThreadService<D extends Thread, R, M extends BaseMapper<D, R, Long>, C> implements ThreadService<R, PostResource> {
    private final GenericThreadService<D, C> service;
    private final M threadMapper;
    private final PostMapper postMapper;

    public MappingThreadService(ThreadRepository<D> threadRepository, M threadMapper, PostMapper postMapper, Class<C> context) {
        this.service = new GenericThreadService<>(threadRepository, context);
        this.threadMapper = threadMapper;
        this.postMapper = postMapper;
    }

    public ServiceResult<List<R>> findAll(Long contextClassId) {
        return service.findAll(contextClassId)
                .andOnSuccessReturn(queries -> simpleMap(queries, threadMapper::mapToResource));
    }

    public ServiceResult<R> findOne(Long id) {
        return service.findOne(id).andOnSuccessReturn(threadMapper::mapToResource);
    }

    public ServiceResult<Long> create(R query) {
        return service.create(threadMapper.mapToDomain(query));
    }

    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(postMapper.mapToDomain(post), threadId);
    }
}