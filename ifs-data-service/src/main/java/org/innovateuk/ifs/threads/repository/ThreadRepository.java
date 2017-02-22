package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.threads.domain.Thread;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThreadRepository<T extends Thread> extends PagingAndSortingRepository<T, Long> {
    List<T> findAllByClassPkAndClassName(Long classPk, String className);

    static final String FIND_ONE_THAT_HOLDS_ATTACHMENT =
            "SELECT DISTINCT t from Thread t " +
            "JOIN t.posts threadPost " +
            "JOIN threadPost.attachments postAttachment " +
            "WHERE postAttachment.id = :attachmentId";

    @Query(FIND_ONE_THAT_HOLDS_ATTACHMENT)
    List<T> findOneThatHoldsAttachment(@Param("attachmentId") Long attachmentId);
}