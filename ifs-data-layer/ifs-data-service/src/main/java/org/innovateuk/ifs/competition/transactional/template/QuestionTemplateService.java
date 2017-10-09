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
import java.util.List;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class QuestionTemplateService {
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

    @Transactional
    public ServiceResult<Question> addDefaultQuestionToCompetition(Competition competition) {
        Section applicationQuestionsSection = sectionRepository.findByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildQuestion(competition);
        return serviceSuccess(createQuestionFunction(competition, applicationQuestionsSection).apply(question));
    }

    @Transactional
    public List<Question> createQuestions(Competition competition, Section section, List<Question> questions) {
        return simpleMap(questions, createQuestionFunction(competition, section));
    }

    @Transactional
    public void cleanForCompetition(Competition competition) {
        formInputTemplateService.cleanForCompetition(competition);

        List<Question> questions = questionRepository.findByCompetitionId(competition.getId());
        questionRepository.delete(questions);
    }

    private Function<Question, Question> createQuestionFunction(Competition competition, Section section) {
        return (Question question) -> {
            entityManager.detach(question);
            question.setCompetition(competition);
            question.setSection(section);
            question.setId(null);
            questionRepository.save(question);

            question.setFormInputs(formInputTemplateService.createFormInputs(competition, question, question.getFormInputs()));
            return question;
        };
    }
}