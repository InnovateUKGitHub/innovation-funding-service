package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormInputResponseRepository extends PagingAndSortingRepository<FormInputResponse, Long> {
    List<FormInputResponse> findByUpdatedById(@Param("updatedById") Long updatedById);
    List<FormInputResponse> findByApplicationIdAndFormInputId(@Param("applicationId") Long applicationId, @Param("formInputId") Long formInputId);
    FormInputResponse findOneByApplicationIdAndFormInputQuestionName(long applicationId, String formInputQuestionName);
    List<FormInputResponse> findByApplicationIdAndFormInputQuestionId(long applicationId, long questionId);
    List<FormInputResponse> findByApplicationId(@Param("applicationId") Long applicationId);
    FormInputResponse findByApplicationIdAndUpdatedByIdAndFormInputId(@Param("applicationId") Long applicationId, @Param("updatedById") Long updatedById, @Param("formInputId") Long formInputId);
    // TODO: Implement this to fix permission issue with file upload - INFUND-2059
    //FormInputResponse findByApplicationIdAndAssignedToIdAndFormInputId(@Param("applicationId") Long applicationId, @Param("assignedToId") Long assignedToId, @Param("formInputId") Long formInputId);
    @Override
    List<FormInputResponse> findAll();
}
