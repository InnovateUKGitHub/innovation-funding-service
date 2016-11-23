package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentTotalScore;
import com.worth.ifs.workflow.repository.ProcessRepository;
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
public interface AssessmentRepository extends ProcessRepository<Assessment>, PagingAndSortingRepository<Assessment, Long> {

    @Override
    Set<Assessment> findAll();

    Assessment findOneByParticipantId(Long participantId);

    List<Assessment> findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(Long userId, Long competitionId);

    @Query(value = "SELECT CASE WHEN COUNT(fi.id) = 0" +
            "  THEN 'TRUE'" +
            "       ELSE 'FALSE' END AS feedback_complete " +
            "FROM application a" +
            "  INNER JOIN competition c" +
            "    ON a.competition = c.id" +
            "  INNER JOIN process p" +
            "    ON a.id = p.target_id" +
            "  INNER JOIN question q" +
            "    ON q.competition_id = c.id" +
            "  INNER JOIN form_input fi" +
            "    ON fi.question_id = q.id " +
            "WHERE NOT EXISTS(" +
            "    SELECT 1 AS response" +
            "    FROM process p" +
            "      INNER JOIN assessor_form_input_response afir" +
            "        ON afir.assessment_id = p.id" +
            "    WHERE afir.value IS NOT NULL" +
            "          AND afir.form_input_id = fi.id" +
            "          AND p.id = :id" +
            ")" +
            "      AND fi.scope = 'ASSESSMENT'" +
            "      AND p.id = :id", nativeQuery = true)
    boolean isFeedbackComplete(@Param("id") Long id);

    @Query(value = "SELECT" +
            "  SUM(afir.value)                                                 AS total_score_given," +
            "  SUM(q.assessor_maximum_score)                                   AS total_score_possible," +
            "  ROUND(100 * SUM(afir.value) / SUM(q.assessor_maximum_score), 1) AS total_score_percentage " +
            "FROM application a" +
            "  INNER JOIN competition c" +
            "    ON a.competition = c.id" +
            "  INNER JOIN process p" +
            "    ON a.id = p.target_id" +
            "  INNER JOIN question q" +
            "    ON c.id = q.competition_id" +
            "  INNER JOIN form_input fi" +
            "    ON q.id = fi.question_id" +
            "  LEFT JOIN assessor_form_input_response afir" +
            "    ON p.id = afir.assessment_id AND fi.id = afir.form_input_id " +
            "WHERE fi.form_input_type_id = (SELECT fit.id" +
            "                               FROM form_input_type fit" +
            "                               WHERE fit.title = 'assessor_score')" +
            "      AND p.id = :id", nativeQuery = true)
    //TODO INFUND-3725 Remove me
    List<AssessmentTotalScore> getTotalScoreNative(@Param("id") Long id);

    @Query(value = "SELECT new com.worth.ifs.assessment.domain.AssessmentTotalScore('foo') " +
            //"  CAST(SUM(afir.value) AS int)," +
            //"1, " +
            //"1" +
            //"  CAST(SUM(q.assessorMaximumScore) AS int)" +
            //") " +
            "FROM Process p" +
            "  JOIN p.target.competition.questions q" +
            "  JOIN q.formInputs fi" +
            "  LEFT JOIN fi.assessorResponses afir " +
            "WHERE fi.formInputType.title = 'assessor_score'" +
            "  AND afir.assessment = p" +
            "  AND p.id = :id")
    AssessmentTotalScore getTotalScore(@Param("id") Long id);
}