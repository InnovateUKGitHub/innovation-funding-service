package org.innovateuk.ifs.workflow.repository;

import java.util.List;

/**
 * Represents a generic interface for any Process-subclass JPA repository.  With common use case methods available
 */
public interface ProcessRepository<T> {

    T findOneByParticipantId(Long participantId);

    T findOneByTargetId(Long targetId);

    T save(T instance);

    List<T> findByTargetId(long targetId);
}
