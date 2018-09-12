package org.innovateuk.ifs.survey.repository;

import org.innovateuk.ifs.survey.domain.Survey;
import org.springframework.data.repository.CrudRepository;

public interface SurveyRepository extends CrudRepository<Survey, Long> {
}
