package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Recommendation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends PagingAndSortingRepository<Recommendation, Long> {

    Recommendation findById(@Param("id") Long id);

    Set<Recommendation> findAll();

    Recommendation findOneByAssessorIdAndApplicationId(Long assessorId, Long applicationId);

    @Query("Select a from Recommendation a where a.assessor.id = ? and a.application.competition.id = ? and a.submitted = true")
    Set<Recommendation> findSubmittedAssessmentsByCompetition(Long assessor, Long competition );

    @Query("Select count(a) from Recommendation a where a.assessor.id = ? and a.application.competition.id = ? and a.submitted = true")
    Integer findNumberOfSubmittedAssessmentsByCompetition(Long assessor, Long competition );

    @Query("Select count(a) from Recommendation a where a.assessor.id = ? and a.application.competition.id = ? and a.status != 'REJECTED' " )
    Integer findNumberOfAssignedAssessmentsByCompetition(Long assessor, Long competition);

    @Query("Select a from Recommendation a where a.assessor.id = ? and a.application.competition.id = ? and a.status = ? order by a.overallScore ASC")
    List<Recommendation> findByAssessorAndCompetitionAndStatus(Long assessor, Long competition, String status );


    @Query("Select a from Recommendation a where a.assessor.id = ? and a.application.competition.id = ? and a.status='ACCEPTED' and a.recommendedValue = 'EMPTY' ")
    List<Recommendation> findOpenByAssessorAndCompetition(Long assessor, Long competition );

    @Query("Select a from Recommendation a where a.assessor.id = ? and a.application.competition.id = ? and a.status='ACCEPTED' and a.recommendedValue != 'EMPTY' ")
    List<Recommendation> findStartedByAssessorAndCompetition(Long assessor, Long competition );

    List<Recommendation> findByAssessorIdAndApplicationCompetitionIdAndStatusIn(Long assessorId, Long competitionId, Set<String> status);
}
