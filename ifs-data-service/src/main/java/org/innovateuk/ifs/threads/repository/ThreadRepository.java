package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.threads.domain.Thread;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThreadRepository<T extends Thread> extends PagingAndSortingRepository<T, Long> {
    static final String FIND_ONE_THAT_HOLDS_ATTACHMENT = "SELECT t from Thread t, Post p, Attachment a " +
            "WHERE a.id = :attachmentId AND a.post_id = p.id AND p.thread_id = t.id";

    List<T> findAllByClassPkAndClassName(Long classPk, String className);

    @Query(FIND_ONE_THAT_HOLDS_ATTACHMENT)
    List<T> findOneThatHoldsAttachment(@Param("attachmentId") Long attachmentId);
}
