package org.innovateuk.ifs.threads.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;


public interface ThreadService<E, P> {

    ServiceResult<List<E>> findAll(Long contextClassPk);

    ServiceResult<E> findOne(Long threadId);

    ServiceResult<Long> create(E e);

    ServiceResult<Void> addPost(P post, Long threadId);
}