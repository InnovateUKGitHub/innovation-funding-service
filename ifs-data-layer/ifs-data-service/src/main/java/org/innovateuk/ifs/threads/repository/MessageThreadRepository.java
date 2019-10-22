package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.threads.domain.MessageThread;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageThreadRepository<T extends MessageThread> extends PagingAndSortingRepository<T, Long> {
    List<T> findAllByClassPkAndClassName(Long classPk, String className);
    List<T> findDistinctThreadByPostsAttachmentsId(@Param("attachmentId") Long attachmentId);
    void deleteAllByClassPk(long projectFianceId);
}