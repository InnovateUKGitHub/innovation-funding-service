package org.innovateuk.ifs.question.transactional.template;

import org.innovateuk.ifs.competition.transactional.template.BaseChainedTemplatePersistor;
import org.innovateuk.ifs.competition.transactional.template.BaseTemplatePersistor;
import org.innovateuk.ifs.competition.transactional.template.FormInputTemplatePersistorImpl;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
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

    @Autowired
    private SetupStatusRepository setupStatusRepository;

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
        deleteQuestion(questionRepository.findOne(questionId));
    }

    public void deleteQuestion(Question question) {
        formInputTemplateService.cleanForParentEntity(question);

        entityManager.detach(question);

        setupStatusRepository.deleteByClassNameAndClassPk(Question.class.getName(), question.getId());
        questionRepository.delete(question.getId());
    }

    @Transactional
    public void cleanForParentEntity(Section section) {
        List<Question> questions = section.getQuestions();
        if(questions != null) {
            questions.stream().forEach(question -> deleteQuestion(question));
        }
    }

    private Function<Question, Question> createQuestionFunction() {
        return (Question question) -> {
            ArrayList<FormInput> formInputsCopy = new ArrayList<>(question.getFormInputs());

            entityManager.detach(question);
            question.setId(null);
            question.setFormInputs(formInputsCopy);
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