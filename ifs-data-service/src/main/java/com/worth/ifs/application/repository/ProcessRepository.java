package com.worth.ifs.application.repository;

import com.worth.ifs.workflow.domain.Process;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public interface ProcessRepository extends PagingAndSortingRepository<Process, Long> {
    Process findById(@Param("id") Long id);
    List<IProcess> findByInvolvedId(@Param("involvedId") Long involvedId);
    List<IProcess> findByTargetId(@Param("targetId") Long targetId);
    List<IProcess> findByInvolvedIdAndType(@Param("involvedId") Long involvedId, @Param("type") ProcessType type );
    List<IProcess> findByTargetIdAndType(@Param("targetId") Long targetId, @Param("type") ProcessType type );

    List<IProcess> findByInvolvedIdAndTypeAndStatus(@Param("targetId") Long targetId,
                                                    @Param("type") ProcessType type,  @Param("status") ProcessStatus status );

    List<Process> findAll();
}



