package org.innovateuk.ifs.questionnaire.link.repository;

import org.innovateuk.ifs.questionnaire.link.domain.ApplicationOrganisationQuestionnaireResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationOrganisationQuestionnaireResponseRepository extends CrudRepository<ApplicationOrganisationQuestionnaireResponse, Long> {

    boolean existsByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(long applicationId, long organisationId, long questionnaireId);

    @Query("select case when count(aoqr)> 0 then true else false end " +
            "from ApplicationOrganisationQuestionnaireResponse aoqr " +
            "JOIN ProcessRole pr on pr.applicationId = aoqr.application.id and pr.organisationId = aoqr.organisation.id " +
            "WHERE aoqr.questionnaireResponse.id = :questionnaireResponseId " +
            "AND pr.user.id = :userId " +
            "AND pr.role in (org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT, org.innovateuk.ifs.user.resource.ProcessRoleType.COLLABORATOR)"
    )
    boolean userCanEditQuestionnaireResponse(UUID questionnaireResponseId, long userId);

    Optional<ApplicationOrganisationQuestionnaireResponse> findByQuestionnaireResponseId(UUID questionnaireResponseId);

    Optional<ApplicationOrganisationQuestionnaireResponse> findByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(long applicationId, long organisationId, long questionnaireId);
}
