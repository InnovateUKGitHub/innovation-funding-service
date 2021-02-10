package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireOptionRepository;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireQuestionResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireQuestionResponseRepository;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class QuestionnaireQuestionResponseServiceImpl extends AbstractIfsCrudServiceImpl<QuestionnaireQuestionResponseResource, QuestionnaireQuestionResponse, Long> implements QuestionnaireQuestionResponseService {
    @Autowired
    private QuestionnaireQuestionResponseRepository questionnaireQuestionResponseRepository;
    @Autowired
    private QuestionnaireOptionRepository questionnaireOptionRepository;
    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Override
    protected CrudRepository<QuestionnaireQuestionResponse, Long> crudRepository() {
        return questionnaireQuestionResponseRepository;
    }

    @Override
    protected Class<QuestionnaireQuestionResponse> getDomainClazz() {
        return QuestionnaireQuestionResponse.class;
    }

    @Override
    protected QuestionnaireQuestionResponse mapToDomain(QuestionnaireQuestionResponse domain, QuestionnaireQuestionResponseResource resource) {
        if (domain.getQuestionnaireResponse() == null) {
            domain.setQuestionnaireResponse(questionnaireResponseRepository.findById(resource.getQuestionnaireResponse()).orElse(null));
        }
        domain.setOption(questionnaireOptionRepository.findById(resource.getOption()).orElse(null));
        return domain;
    }

    @Override
    public ServiceResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(long questionId, long responseId) {
        return find(questionnaireQuestionResponseRepository.findByOptionQuestionIdAndQuestionnaireResponseId(questionId, responseId),
                notFoundError(QuestionnaireQuestionResponse.class, questionId, responseId))
                .andOnSuccessReturn(mapper::mapToResource);
    }
}
