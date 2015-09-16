package com.worth.ifs.application.repository;

import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessEvent;
import com.worth.ifs.workflow.domain.ProcessStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public interface ProcessRepository extends PagingAndSortingRepository<Process, Long> {
    List<Process> findByAssigneeId(@Param("assigneeId") Long assigneeId);
    List<Process> findBySubjectId(@Param("subjectId") Long subjectId);
    List<Process> findByAssigneeIdAndEvent(@Param("assigneeId") Long assigneeId, @Param("event") ProcessEvent event );


    List<Process> findBySubjectIdAndEvent(@Param("subject") Long subjectId, @Param("event") ProcessEvent event );

    List<Process> findByAssigneeIdAndEventAndStatus(@Param("assigneeId") Long assigneeId,
                                                    @Param("event") ProcessEvent event,
                                                   @Param("status") ProcessStatus status );

    List<Process> findAll();
}



