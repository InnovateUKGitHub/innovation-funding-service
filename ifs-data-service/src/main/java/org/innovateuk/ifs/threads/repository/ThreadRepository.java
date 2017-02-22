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
            "JOIN FETCH t.posts threadPost " +
            "JOIN FETCH threadPost.attachments postAttachment " +
            "WHERE postAttachment.id = :attachmentId";

    @Query(FIND_ONE_THAT_HOLDS_ATTACHMENT)
    List<T> findOneThatHoldsAttachment(@Param("attachmentId") Long attachmentId);
}


/*

from Products p INNER JOIN p.productlanguages pl
   where pl.languages.shortname ='eng'


    "SELECT DISTINCT o " +
    "FROM Organization o, User u " +
    "JOIN o.roles oRole " +
    "JOIN u.roles uRole " +
    "WHERE oRole.id = uRole.id AND u.id = :uId")
 */