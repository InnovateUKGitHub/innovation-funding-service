package org.innovateuk.ifs.workflow.audit;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProcessHistoryRepository extends CrudRepository<ProcessHistory, Long> {

    void deleteByProcessId(long processId);

    List<ProcessHistory> findByProcessId(long processId);
}