package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Thread;

import java.util.List;

public interface ThreadService<E, P> {

    ServiceResult<List<E>> findAll(Long contextClassPk);

    ServiceResult<E> findOne(Long contextClassPk);

    ServiceResult<Long> create(E e);

    ServiceResult<Void> addPost(P post, Long threadId);
}