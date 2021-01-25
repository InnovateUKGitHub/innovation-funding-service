package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface MultipleChoiceOptionRepository extends CrudRepository<MultipleChoiceOption, Long> {
}
