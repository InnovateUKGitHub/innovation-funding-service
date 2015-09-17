package com.worth.ifs.application.repository;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.AssessmentProcess;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


/**
 * Created by nunoalexandre on 15/09/15.
 */
public interface AssessmentProcessRepository extends PagingAndSortingRepository<AssessmentProcess, Long> {
//    List<Process> findByAssigneeId(@Param("assigneeId") Long assigneeId);
//    List<Process> findBySubjectId(@Param("subjectId") Long subjectId);
//    List<Process> findByAssigneeIdAndEvent(@Param("assigneeId") Long assigneeId, @Param("event") ProcessEvent event );
//
//
//    List<Process> findBySubjectIdAndEvent(@Param("subject") Long subjectId, @Param("event") ProcessEvent event );
//
//    List<Process> findByAssigneeIdAndEventAndStatus(@Param("assigneeId") Long assigneeId,
//                                                    @Param("event") ProcessEvent event,
//                                                   @Param("status") ProcessStatus status );

    List<AssessmentProcess> findById(Long id);
    List<AssessmentProcess> findByAssessor(User assessor);
    List<AssessmentProcess> findByAssessorAndApplicationCompetition(User assessor, Competition competition);
    //Process> findAll();
}



