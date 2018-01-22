package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Thread;
import org.innovateuk.ifs.threads.repository.ThreadRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

//TODO - Rogier mentioned that he was going to remove RootTransactionalService but this has not yet been done
public class GenericThreadService<E extends Thread, C> extends RootTransactionalService implements ThreadService<E, Post> {
    private final ThreadRepository<E> repository;
    private final Class<C> contextClass;

    @Autowired
    protected UserRepository userRepository;

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

/*    @Override
    public ServiceResult<Void> close(Long threadId, Long userId) {

        return find(() -> find(repository.findOne(threadId), notFoundError(Thread.class, threadId)), () -> find(userRepository.findOne(userId), notFoundError(User.class, userId)))
                .andOnSuccess((thread, user) -> {
                    thread.closeThread(user);
                    repository.save(thread);
                    return serviceSuccess();
                });
    }*/

    @Override
    public ServiceResult<Void> close(Long threadId) {

        return find(repository.findOne(threadId), notFoundError(Thread.class))
                .andOnSuccessReturnVoid(thread -> getCurrentlyLoggedInUser()
                        .andOnSuccess(currentUser -> {
                            thread.closeThread(currentUser);
                            repository.save(thread);
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
