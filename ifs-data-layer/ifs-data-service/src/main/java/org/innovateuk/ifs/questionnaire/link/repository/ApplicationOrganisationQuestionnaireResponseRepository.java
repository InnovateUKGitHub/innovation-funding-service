package org.innovateuk.ifs.questionnaire.link.repository;

import org.innovateuk.ifs.questionnaire.link.domain.ApplicationOrganisationQuestionnaireResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationOrganisationQuestionnaireResponseRepository extends CrudRepository<ApplicationOrganisationQuestionnaireResponse, Long> {

    boolean existsByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(long applicationId, long organisationId, long questionnaireId);
    Optional<ApplicationOrganisationQuestionnaireResponse> findByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(long applicationId, long organisationId, long questionnaireId);
}
