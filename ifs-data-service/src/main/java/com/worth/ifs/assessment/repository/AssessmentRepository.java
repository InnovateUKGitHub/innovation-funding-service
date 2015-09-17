package com.worth.ifs.assessment.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
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
    List<Assessment> findByProcessAssessor(User assessor);
    List<Assessment> findByProcessAssessorAndProcessApplicationCompetition(User assessor, Competition competition);

    @Query("Select a from Assessment a, Process p where a.process = p and p.assessor = ? and p.application.competition = ? and a.submitted = true")
    List<Assessment> findSubmittedAssessmentsByCompetition(User assessor, Competition competition );


}
