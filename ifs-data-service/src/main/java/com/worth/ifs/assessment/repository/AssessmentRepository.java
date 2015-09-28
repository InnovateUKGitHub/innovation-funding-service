package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.workflow.domain.ProcessStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


/**
 * Created by nunoalexandre on 16/09/15.
 */

public interface AssessmentRepository extends PagingAndSortingRepository<Assessment, Long> {

    Assessment findById(@Param("id") Long id);

    Set<Assessment> findAll();

    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.id = ?")
    Assessment findOneByAssessorAndApplication(Long assessorId, Long applicationId);

    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.submitted = true")
    Set<Assessment> findSubmittedAssessmentsByCompetition(Long assessor, Long competition );

    @Query("Select count(a) from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.submitted = true")
    Integer findNumberOfSubmittedAssessmentsByCompetition(Long assessor, Long competition );

    @Query("Select count(a) from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.status != 'REJECTED' " )
    Integer findNumberOfAssignedAssessmentsByCompetition(Long assessor, Long competition);

    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.status = ? order by a.overallScore ASC")
    List<Assessment> findByAssessorAndCompetitionAndStatus(Long assessor, Long competition, ProcessStatus status );


    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.status='ACCEPTED' and a.recommendedValue = 'EMPTY' ")
    List<Assessment> findOpenByAssessorAndCompetition(Long assessor, Long competition );

    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.status='ACCEPTED' and a.recommendedValue != 'EMPTY' ")
    List<Assessment> findStartedByAssessorAndCompetition(Long assessor, Long competition );
}
