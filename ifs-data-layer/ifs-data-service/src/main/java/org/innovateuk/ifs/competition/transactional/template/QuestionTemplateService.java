package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class QuestionTemplateService implements BaseTemplateService<List<Question>, Section> {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private FormInputTemplateService formInputTemplateService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DefaultApplicationQuestionFactory defaultApplicationQuestionFactory;

    public ServiceResult<List<Question>> createDefaultForApplicationSection(Competition competition) {
        Section applicationQuestionsSection = sectionRepository.findByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);

        return createByTemplate(Arrays.asList(question));
    }

    @Transactional
    public ServiceResult<List<Question>> createByTemplate(List<Question> questions) {
        return serviceSuccess(simpleMap(questions, createQuestionFunction()));
    }

    @Transactional
    public List<Question> createByRequisite(Section section) {
        return simpleMap(section.getQuestions(), createQuestionFunction(section));
    }

    @Transactional
    public void cleanForRequisite(Section section) {
        formInputTemplateService.cleanForCompetition(section.getCompetition());

        List<Question> questions = questionRepository.findByCompetitionId(section.getCompetition().getId());
        questionRepository.delete(questions);
    }

    private Function<Question, Question> createQuestionFunction() {
        return (Question question) -> {
            entityManager.detach(question);
            question.setId(null);
            questionRepository.save(question);

            question.setFormInputs(formInputTemplateService.createByRequisite(question));
            return question;
        };
    }

    private Function<Question, Question> createQuestionFunction(Section section) {
        return (Question question) -> {
            entityManager.detach(question);
            question.setCompetition(section.getCompetition());
            question.setSection(section);
            question.setId(null);
            questionRepository.save(question);

            question.setFormInputs(formInputTemplateService.createByRequisite(question));
            return question;
        };
    }
}