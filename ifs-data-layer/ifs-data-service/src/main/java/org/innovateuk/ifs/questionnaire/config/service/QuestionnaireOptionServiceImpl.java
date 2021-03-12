package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireOption;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireQuestion;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireTextOutcome;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireOptionRepository;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireQuestionRepository;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireTextOutcomeRepository;
import org.innovateuk.ifs.questionnaire.resource.DecisionType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class QuestionnaireOptionServiceImpl extends AbstractIfsCrudServiceImpl<QuestionnaireOptionResource, QuestionnaireOption, Long> implements QuestionnaireOptionService {

    @Autowired
    private QuestionnaireOptionRepository questionnaireOptionRepository;

    @Autowired
    private QuestionnaireQuestionRepository questionnaireQuestionRepository;

    @Autowired
    private QuestionnaireTextOutcomeRepository questionnaireTextOutcomeRepository;

    @Override
    protected CrudRepository<QuestionnaireOption, Long> crudRepository() {
        return questionnaireOptionRepository;
    }

    @Override
    protected Class<QuestionnaireOption> getDomainClazz() {
        return QuestionnaireOption.class;
    }

    @Override
    protected QuestionnaireOption mapToDomain(QuestionnaireOption domain, QuestionnaireOptionResource resource) {
        if (domain.getQuestion() == null) {
            domain.setQuestion(questionnaireQuestionRepository.findById(resource.getQuestion()).orElseThrow(ObjectNotFoundException::new));
        }
        if (resource.getDecisionType() == DecisionType.QUESTION) {
            if (domain.getDecision() == null || !domain.getDecision().getId().equals(resource.getDecision())) {
                QuestionnaireQuestion question = questionnaireQuestionRepository.findById(resource.getDecision()).orElseThrow(ObjectNotFoundException::new);
                domain.setDecision(question);
                question.setDepth(calculateDepth(question));
            }
        } else if (resource.getDecisionType() == DecisionType.TEXT_OUTCOME) {
            if (domain.getDecision()== null || !domain.getDecision().getId().equals(resource.getDecision())) {
                QuestionnaireTextOutcome outcome = questionnaireTextOutcomeRepository.findById(resource.getDecision()).orElseThrow(ObjectNotFoundException::new);
                domain.setDecision(outcome);
            }
        }
        domain.setText(resource.getText());
        return domain;
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(Long id) {
        return get(id)
                .andOnSuccess(option -> {
                    if (option.getDecisionType() == DecisionType.TEXT_OUTCOME) {
                        questionnaireTextOutcomeRepository.deleteById(option.getDecision());
                    }
                    return serviceSuccess();
                })
                .andOnSuccess(() -> super.delete(id));
    }

    private int calculateDepth(QuestionnaireQuestion question) {
        return question.getOptionsLinkedToThisDecision().stream().map(
                QuestionnaireOption::getQuestion
        )
                .map(QuestionnaireQuestion::getDepth)
                .max(Integer::compareTo).orElse(0) + 1;

    }
}
