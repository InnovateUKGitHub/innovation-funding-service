package com.worth.ifs.workflow.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Represents a generic interface for any Process-subclass JPA repository.  With common use case methods available
 */
public interface ProcessRepository<T> extends PagingAndSortingRepository<T, Long> {

    T findOneByParticipantId(Long participantId);

    T findOneByTargetId(Long targetId);
}
