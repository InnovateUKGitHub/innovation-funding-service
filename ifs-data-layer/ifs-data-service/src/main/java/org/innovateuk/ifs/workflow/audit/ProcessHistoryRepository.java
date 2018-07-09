package org.innovateuk.ifs.workflow.audit;


import org.springframework.data.repository.CrudRepository;

public interface ProcessHistoryRepository extends CrudRepository<ProcessHistory, Long> {
}