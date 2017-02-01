package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.threads.domain.Thread;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ThreadRepository<T extends Thread> extends PagingAndSortingRepository<T, Long> {
    T findByClassPkAndClassName(Long classPk, String className);
}
