package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.link.domain.ApplicationOrganisationQuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.link.repository.ApplicationOrganisationQuestionnaireResponseRepository;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class QuestionnaireResponseLinkServiceImpl extends BaseTransactionalService implements QuestionnaireResponseLinkService {

    @Autowired
    private ApplicationOrganisationQuestionnaireResponseRepository applicationOrganisationQuestionnaireResponseRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Override
    public ServiceResult<UUID> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long applicationId, long organisationId, long questionnaireId) {
        if (applicationOrganisationQuestionnaireResponseRepository.existsByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(applicationId, organisationId, questionnaireId)) {
            return find(applicationOrganisationQuestionnaireResponseRepository.findByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(applicationId, organisationId, questionnaireId),
                    notFoundError(ApplicationOrganisationQuestionnaireResponse.class, applicationId, organisationId, questionnaireId))
                    .andOnSuccessReturn(ApplicationOrganisationQuestionnaireResponse::getQuestionnaireResponse)
                    .andOnSuccessReturn(QuestionnaireResponse::getId);
        }
        return find(application(applicationId), organisation(organisationId), questionnaire(questionnaireId))
                .andOnSuccess((application, organisation, questionnaire) -> {
                    QuestionnaireResponse response = new QuestionnaireResponse();
                    response.setQuestionnaire(questionnaire);
                    ApplicationOrganisationQuestionnaireResponse applicationOrganisationQuestionnaireResponse = new ApplicationOrganisationQuestionnaireResponse();
                    applicationOrganisationQuestionnaireResponse.setApplication(application);
                    applicationOrganisationQuestionnaireResponse.setOrganisation(organisation);
                    applicationOrganisationQuestionnaireResponse.setQuestionnaireResponse(questionnaireResponseRepository.save(response));
                    applicationOrganisationQuestionnaireResponseRepository.save(applicationOrganisationQuestionnaireResponse);
                    return serviceSuccess(response.getId());
                });
    }

    protected Supplier<ServiceResult<Questionnaire>> questionnaire(long id) {
        return () ->  find(questionnaireRepository.findById(id), notFoundError(Questionnaire.class, id));
    }



}
