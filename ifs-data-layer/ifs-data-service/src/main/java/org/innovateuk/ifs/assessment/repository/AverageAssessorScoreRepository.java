package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface AverageAssessorScoreRepository extends CrudRepository<AverageAssessorScore, Long> {

    Optional<AverageAssessorScore> findByApplicationId(long applicationId);
}
