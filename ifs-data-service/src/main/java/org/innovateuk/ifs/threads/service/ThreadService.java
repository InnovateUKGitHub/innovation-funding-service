package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Thread;

import java.util.List;


public interface ThreadService<E, P> {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = true)
    ServiceResult<List<E>> findAll(Long contextClassPk);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = true)
    ServiceResult<E> findOne(Long contextClassPk);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = true)
    ServiceResult<Long> create(E e);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = true)
    ServiceResult<Void> addPost(P post, Long threadId);
}