package org.innovateuk.ifs.questionnaire.link.repository;

import org.innovateuk.ifs.questionnaire.link.domain.ProjectOrganisationQuestionnaireResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectOrganisationQuestionnaireResponseRepository extends CrudRepository<ProjectOrganisationQuestionnaireResponse, Long> {

    boolean existsByProjectIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(long projectId, long organisationId, long questionnaireId);

    Optional<ProjectOrganisationQuestionnaireResponse> findByQuestionnaireResponseId(UUID questionnaireResponseId);

    Optional<ProjectOrganisationQuestionnaireResponse> findByProjectIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(long projectId, long organisationId, long questionnaireId);
}
