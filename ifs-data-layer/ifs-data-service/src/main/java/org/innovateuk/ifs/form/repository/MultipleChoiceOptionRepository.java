package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface MultipleChoiceOptionRepository extends CrudRepository<MultipleChoiceOption, Long> {
}
