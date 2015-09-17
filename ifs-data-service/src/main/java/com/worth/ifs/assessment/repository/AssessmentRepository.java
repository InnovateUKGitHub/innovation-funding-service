package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.ProcessStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;


/**
 * Created by nunoalexandre on 16/09/15.
 */

public interface AssessmentRepository extends PagingAndSortingRepository<Assessment, Long> {

    Assessment findById(@Param("id") Long id);

    List<Assessment> findAll();

    List<Assessment> findByProcessAssessorId(Long assessorId);

    @Query("Select a from Assessment a, Process p where a.process.id = p.id and p.assessor.id = ? and p.application.competition.id = ?")
    List<Assessment> findByAssessorAndCompetition(Long assessorId, Long competitionId);

    @Query("Select a from Assessment a, Process p where a.process.id = p.id and p.assessor.id = ? and p.application.competition.id = ? and a.submitted = true")
    List<Assessment> findSubmittedAssessmentsByCompetition(Long assessor, Long competition );

    @Query("Select count(a) from Assessment a, Process p where a.process.id = p.id and p.assessor.id = ? and p.application.competition.id = ? and a.submitted = true")
    Integer findNumberOfSubmittedAssessmentsByCompetition(Long assessor, Long competition );

    @Query("Select count(a) from Assessment a, Process p where a.process.id = p.id and p.assessor.id = ? and p.application.competition.id = ? and p.status != ?")
    Integer findNumberOfAssignedAssessmentsByCompetition(Long assessor, Long competition, ProcessStatus status );

    @Query("Select a from Assessment a, Process p where a.process.id = p.id and p.assessor.id = ? and p.application.competition.id = ? and p.status = ?")
    List<Assessment> findAssessmentsByCompetitionAndStatus(Long assessor, Long competition, ProcessStatus status );

    //findNumberOfAssignedAssessmentsByCompetition
}
