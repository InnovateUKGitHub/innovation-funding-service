package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


/**
 * AssessmentRepository is an interface between the outside and the ORM Assessment interaction.
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
    List<Assessment> findByAssessorAndCompetitionAndStatus(Long assessor, Long competition, String status );


    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.status='ACCEPTED' and a.recommendedValue = 'EMPTY' ")
    List<Assessment> findOpenByAssessorAndCompetition(Long assessor, Long competition );

    @Query("Select a from Assessment a where a.assessor.id = ? and a.application.competition.id = ? and a.status='ACCEPTED' and a.recommendedValue != 'EMPTY' ")
    List<Assessment> findStartedByAssessorAndCompetition(Long assessor, Long competition );

    List<Assessment> findByAssessorIdAndApplicationCompetitionIdAndStatusIn(Long assessorId, Long competitionId, Set<String> status);
}
