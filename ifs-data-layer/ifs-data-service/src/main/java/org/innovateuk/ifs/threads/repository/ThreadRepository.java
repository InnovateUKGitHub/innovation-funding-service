package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.threads.domain.Thread;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThreadRepository<T extends Thread> extends PagingAndSortingRepository<T, Long> {
    List<T> findAllByClassPkAndClassName(Long classPk, String className);
    List<T> findDistinctThreadByPostsAttachmentsId(@Param("attachmentId") Long attachmentId);
}