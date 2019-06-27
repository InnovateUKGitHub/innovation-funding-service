package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.MessageThread;
import org.innovateuk.ifs.threads.repository.MessageThreadRepository;
import org.innovateuk.ifs.util.AuthenticationHelper;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public class GenericMessageThreadService<E extends MessageThread, C> implements MessageThreadService<E, Post> {
    private final MessageThreadRepository<E> repository;
    private final Class<C> contextClass;
    private final AuthenticationHelper authenticationHelper;

    GenericMessageThreadService(MessageThreadRepository<E> repository, AuthenticationHelper authenticationHelper, Class<C> contextClassName) {
        this.repository = repository;
        this.contextClass = contextClassName;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public ServiceResult<List<E>> findAll(Long contextClassPk) {
        return find(repository.findAllByClassPkAndClassName(contextClassPk, contextClass.getName()),
                notFoundError(contextClass, contextClassPk));
    }

    @Override
    public ServiceResult<E> findOne(Long id) {
        return find(repository.findById(id), notFoundError(MessageThread.class));
    }

    @Override
    public ServiceResult<Long> create(E e) {
        e.setContext(contextClass.getName());
        return serviceSuccess(repository.save(e).id());
    }

    @Override
    public ServiceResult<Void> close(Long threadId) {

        return find(repository.findById(threadId), notFoundError(MessageThread.class))
                .andOnSuccessReturnVoid(thread -> authenticationHelper.getCurrentlyLoggedInUser()
                        .andOnSuccess(currentUser -> {
                            thread.closeThread(currentUser);
                            return serviceSuccess();
                        }));
    }

    @Override
    public ServiceResult<Void> addPost(Post post, Long threadId) {
        return findOne(threadId).andOnSuccessReturn(thread -> {
            thread.addPost(post);
            return repository.save(thread);
        }).andOnSuccessReturnVoid();
    }
}
