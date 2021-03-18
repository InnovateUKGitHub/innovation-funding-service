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

import javax.ws.rs.NotFoundException;

import java.util.UUID;

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
            domain.setQuestionnaireResponse(questionnaireResponseRepository.findById(UUID.fromString(resource.getQuestionnaireResponse())).orElse(null));
        }
        if (domain.getId() != null && domain.getOption() != null && !domain.getOption().getId().equals(resource.getOption())) {
            resetAnswersDeeperThanThis(resource.getOption(), domain.getQuestionnaireResponse().getId(), domain.getId());
        }
        domain.setOption(questionnaireOptionRepository.findById(resource.getOption()).orElseThrow(NotFoundException::new));
        return domain;
    }

    private void resetAnswersDeeperThanThis(long optionId, UUID questionnaireResponseId, long questionResponseId) {
        int questionDepth = questionnaireOptionRepository.findById(optionId).orElseThrow(NotFoundException::new).getQuestion().getDepth();
        questionnaireQuestionResponseRepository.deleteByQuestionnaireResponseIdAndOptionQuestionDepthGreaterThanEqualAndIdNot(questionnaireResponseId, questionDepth, questionResponseId);
    }

    @Override
    public ServiceResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(long questionId, String responseId) {
        return find(questionnaireQuestionResponseRepository.findByOptionQuestionIdAndQuestionnaireResponseId(questionId, UUID.fromString(responseId)),
                notFoundError(QuestionnaireQuestionResponse.class, questionId, responseId))
                .andOnSuccessReturn(mapper::mapToResource);
    }
}
