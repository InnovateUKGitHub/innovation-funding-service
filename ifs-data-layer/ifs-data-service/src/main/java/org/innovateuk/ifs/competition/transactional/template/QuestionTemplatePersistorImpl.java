package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.function.Function;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;


/**
 * Transactional component providing functions for persisting copies of Questions by their parent Section or Question template entity object.
 */

@Component
public class QuestionTemplatePersistorImpl implements BaseChainedTemplatePersistor<List<Question>, Section>, BaseTemplatePersistor<List<Question>> {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputTemplatePersistorImpl formInputTemplateService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DefaultApplicationQuestionCreator defaultApplicationQuestionCreator;

    @Transactional
    public List<Question> persistByEntity(List<Question> questions) {
        return simpleMap(questions, createQuestionFunction());
    }

    @Transactional
    public List<Question> persistByParentEntity(Section section) {
        return simpleMap(section.getQuestions(), createQuestionFunction(section));
    }

    @Transactional
    public void deleteEntityById(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        formInputTemplateService.cleanForParentEntity(question);

        entityManager.detach(question);
        questionRepository.delete(questionId);
    }

    @Transactional
    public void cleanForParentEntity(Section section) {
        List<Question> questions = section.getQuestions();
        if(questions != null) {
            questions.stream().forEach(question -> formInputTemplateService.cleanForParentEntity(question));

            questions.stream().forEach(question -> entityManager.detach(question));
            questionRepository.delete(questions);
        }
    }

    private Function<Question, Question> createQuestionFunction() {
        return (Question question) -> {
            entityManager.detach(question);
            question.setId(null);
            questionRepository.save(question);

            question.setFormInputs(formInputTemplateService.persistByParentEntity(question));
            return question;
        };
    }

    private Function<Question, Question> createQuestionFunction(Section section) {
        return (Question question) -> {
            question.setCompetition(section.getCompetition());
            question.setSection(section);

            return createQuestionFunction().apply(question);
        };
    }
}