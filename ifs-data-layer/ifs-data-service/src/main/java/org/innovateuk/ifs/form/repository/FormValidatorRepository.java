package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.FormValidator;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormValidatorRepository extends PagingAndSortingRepository<FormValidator, Long> {
	@Override
    List<FormValidator> findAll();
    FormValidator findById(@Param("id") Long id);
    FormValidator findByClazzName(@Param("title") String title);
}
