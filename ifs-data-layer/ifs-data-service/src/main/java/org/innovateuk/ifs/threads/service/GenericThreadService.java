package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Thread;
import org.innovateuk.ifs.threads.repository.ThreadRepository;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public class GenericThreadService<E extends Thread, C> implements ThreadService<E, Post> {
    private final ThreadRepository<E> repository;
    private final Class<C> contextClass;

    GenericThreadService(ThreadRepository<E> repository, Class<C> contextClassName) {
        this.repository = repository;
        this.contextClass = contextClassName;
    }

    @Override
    public ServiceResult<List<E>> findAll(Long contextClassPk) {
        return find(repository.findAllByClassPkAndClassName(contextClassPk, contextClass.getName()),
                notFoundError(contextClass, contextClassPk));
    }

    @Override
    public ServiceResult<E> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(Thread.class));
    }

    @Override
    public ServiceResult<Long> create(E e) {
        e.setContext(contextClass.getName());
        return serviceSuccess(repository.save(e).id());
    }

    @Override
    public ServiceResult<Void> addPost(Post post, Long threadId) {
        return findOne(threadId).andOnSuccessReturn(thread -> {
            thread.addPost(post);
            return repository.save(thread);
        }).andOnSuccessReturnVoid();
    }
}
