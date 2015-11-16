package com.worth.ifs.form.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormInputResponseRepository extends PagingAndSortingRepository<FormInputResponse, Long> {
    List<FormInputResponse> findByUpdatedBy(@Param("updatedBy") ProcessRole updatedBy);
    FormInputResponse findByApplicationAndFormInput(@Param("application") Application application, @Param("formInput") FormInput formInput);
    FormInputResponse findByApplicationIdAndFormInputId(@Param("applicationId") Long applicationId, @Param("formInputId") Long formInputId);
    List<FormInputResponse> findAll();
}