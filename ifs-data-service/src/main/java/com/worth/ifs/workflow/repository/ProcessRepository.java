package com.worth.ifs.workflow.repository;

/**
 * Represents a generic interface for any Process-subclass JPA repository.  With common use case methods available
 */
public interface ProcessRepository<T> {

    T findOneByParticipantId(Long participantId);

    T findOneByTargetId(Long targetId);

    T save(T instance);
}
