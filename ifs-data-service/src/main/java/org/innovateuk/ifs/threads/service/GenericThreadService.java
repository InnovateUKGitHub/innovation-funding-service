package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Thread;
import org.innovateuk.ifs.threads.repository.ThreadRepository;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public class GenericThreadService<E extends Thread, P extends Post, C> implements ThreadService<E, P> {

    private final ThreadRepository<E> repository;
    private final Class<C> contextClass;

    public GenericThreadService(ThreadRepository<E> repository, Class<C> contextClassName) {
        this.repository = repository;
        this.contextClass = contextClassName;
    }

    @Override
    public ServiceResult<List<E>> findAll(Long contextClassPk) {
        return find(repository.findAllByClassPkAndClassName(contextClassPk, contextClass.getName()),
                notFoundError(contextClass, contextClassPk));
    }

    @Override
    public final ServiceResult<E> findOne(Long contextClassPk) {
        return find(repository.findByClassPkAndClassName(contextClassPk, ProjectFinance.class.getName()),
                notFoundError(contextClass, contextClassPk));
    }

    @Override
    public final ServiceResult<Void> create(E e) {
        repository.save(e);
        return serviceSuccess();
    }

    @Override
    public final ServiceResult<Void> addPost(P post, Long threadId) {
        return findOne(threadId).andOnSuccessReturn(thread -> {
            thread.addPost(post);
            return repository.save(thread);
        }).andOnSuccessReturnVoid();
    }
}
