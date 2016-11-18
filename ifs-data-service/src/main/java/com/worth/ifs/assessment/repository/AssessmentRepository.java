package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
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

    @Query(value = "SELECT CASE WHEN COUNT(form_input.id) = 0 " +
            "THEN 'true' " +
            "ELSE 'false' END AS feedback_complete " +
            "FROM application, competition, form_input, process, question " +
            "WHERE NOT EXISTS(" +
            "        SELECT 1" +
            "        FROM process, assessor_form_input_response" +
            "                WHERE assessor_form_input_response.assessment_id = process.id" +
            "                AND assessor_form_input_response.form_input_id = form_input.id" +
            "                AND assessor_form_input_response.value IS NOT NULL" +
            "                AND process.id = :id" +
            ") " +
            "AND application.competition = competition.id "+
            "AND application.id = process.target_id " +
            "AND form_input.scope = 'ASSESSMENT' " +
            "AND form_input.question_id = question.id " +
            "AND question.competition_id = competition.id " +
            "AND process.id = :id", nativeQuery = true)
    boolean isFeedbackComplete(@Param("id") Long id);
}