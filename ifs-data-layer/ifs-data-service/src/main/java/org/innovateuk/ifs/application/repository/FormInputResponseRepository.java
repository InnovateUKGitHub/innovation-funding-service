package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormInputResponseRepository extends PagingAndSortingRepository<FormInputResponse, Long> {
    List<FormInputResponse> findByUpdatedById(@Param("updatedById") Long updatedById);
    List<FormInputResponse> findByApplicationIdAndFormInputId(@Param("applicationId") Long applicationId, @Param("formInputId") Long formInputId);
    FormInputResponse findOneByApplicationIdAndFormInputQuestionName(long applicationId, String formInputQuestionName);
    FormInputResponse findOneByApplicationIdAndFormInputQuestionQuestionSetupType(long applicationId, QuestionSetupType questionSetupType);
    FormInputResponse findOneByApplicationIdAndFormInputDescription(long applicationId, String formInputDescription);
    List<FormInputResponse> findByApplicationIdAndFormInputQuestionId(long applicationId, long questionId);
    List<FormInputResponse> findByApplicationId(@Param("applicationId") Long applicationId);
    FormInputResponse findByApplicationIdAndUpdatedByIdAndFormInputId(@Param("applicationId") Long applicationId, @Param("updatedById") Long updatedById, @Param("formInputId") Long formInputId);

    @Override
    List<FormInputResponse> findAll();

    Optional<FormInputResponse> findByApplicationIdAndFormInputQuestionIdAndUpdatedByOrganisationIdAndFormInputType(long applicationId, long questionId, long organisationId, FormInputType formInputType);

    Optional<FormInputResponse> findByApplicationIdAndFormInputQuestionIdAndUpdatedByOrganisationIdAndFormInputTypeAndFormInputDescription(long applicationId, long questionId, long organisationId, FormInputType formInputType, String description);
    void deleteByApplicationId(long applicationId);
}