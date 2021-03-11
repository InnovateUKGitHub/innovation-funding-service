package org.innovateuk.ifs.workflow.audit;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProcessHistoryRepository extends CrudRepository<ProcessHistory, Long> {

    @Query("DELETE FROM ProcessHistory ph WHERE ph.process.id = :processId")
    void deleteByProcessId(@Param("processId") long processId);
}