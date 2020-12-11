package org.innovateuk.ifs.question.transactional.template;

import org.innovateuk.ifs.application.validator.NotEmptyValidator;
import org.innovateuk.ifs.application.validator.RequiredFileValidator;
import org.innovateuk.ifs.application.validator.RequiredMultipleChoiceValidator;
import org.innovateuk.ifs.application.validator.WordCountValidator;
import org.innovateuk.ifs.assessment.validator.AssessorScopeValidator;
import org.innovateuk.ifs.assessment.validator.AssessorScoreValidator;
import org.innovateuk.ifs.assessment.validator.ResearchCategoryValidator;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.*;
import org.innovateuk.ifs.form.repository.*;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.setup.resource.QuestionSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.setup.resource.QuestionSection.APPLICATION_QUESTIONS;

/**
 * Service that can reorder questions by priority after creation or deletion.
 */
@Service
public class QuestionPriorityOrderService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionNumberOrderService questionNumberOrderService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowRepository guidanceRowRepository;

    @Autowired
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;

    @Autowired
    private FormValidatorRepository formValidatorRepository;

    @Transactional
    @NotSecured("Must be secured by other services.")
    public Question prioritiseResearchCategoryQuestionAfterCreation(Question createdQuestion) {
        // The Research Category question should be prioritised after the Application Details question

        Question applicationDetailsQuestion = questionRepository.findFirstByCompetitionIdAndQuestionSetupType
                (createdQuestion.getCompetition().getId(), QuestionSetupType.APPLICATION_DETAILS);

        QuestionSection questionSection = QuestionSection.findByName(createdQuestion.getSection().getName());
        updateFollowingQuestionsPrioritiesByDelta(1, applicationDetailsQuestion.getPriority(), createdQuestion.getCompetition()
                .getId(), questionSection);

        createdQuestion.setPriority(applicationDetailsQuestion.getPriority() + 1);
        return questionRepository.save(createdQuestion);

    }

    @Transactional
    @NotSecured("Must be secured by other services.")
    public Question prioritiseAssessedQuestionAfterCreation(Question createdQuestion) {
        Question assessedQuestionWithHighestPriority = questionRepository
                .findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(createdQuestion.getCompetition().getId(),
                        APPLICATION_QUESTIONS.getName());
        createdQuestion.setPriority(assessedQuestionWithHighestPriority.getPriority() + 1);

        Question questionSaved = questionRepository.save(createdQuestion);

        questionNumberOrderService.updateAssessedQuestionsNumbers(createdQuestion.getCompetition().getId());

        return questionSaved;
    }

    @Transactional
    @NotSecured("Must be secured by other services.")
    public void reprioritiseQuestionsAfterDeletion(Question deletedQuestion) {
        QuestionSection questionSection = QuestionSection.findByName(deletedQuestion.getSection().getName());
        updateFollowingQuestionsPrioritiesByDelta(-1, deletedQuestion.getPriority(), deletedQuestion.getCompetition().getId(), questionSection);
    }

    private void updateFollowingQuestionsPrioritiesByDelta(int delta,
                                                           Integer priority,
                                                           long competitionId,
                                                           QuestionSection questionSection) {
        List<Question> subsequentQuestions = questionRepository
                .findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(competitionId,
                        questionSection.getName(), priority);

        subsequentQuestions.forEach(question -> question.setPriority(question.getPriority() + delta));

        questionRepository.saveAll(subsequentQuestions);
    }

    @NotSecured("Must be secured by other services.")
    public void persistAndPrioritiseSections(Competition competition, List<Section> sections, Section parent) {
            FormValidator notEmptyValidator = formValidatorRepository.findByClazzName(NotEmptyValidator.class.getName());
            FormValidator wordCountValidator = formValidatorRepository.findByClazzName(WordCountValidator.class.getName());
            FormValidator researchCategoryValidator = formValidatorRepository.findByClazzName(ResearchCategoryValidator.class.getName());
            FormValidator assessorScopeValidator = formValidatorRepository.findByClazzName(AssessorScopeValidator.class.getName());
            FormValidator assessorScoreValidator = formValidatorRepository.findByClazzName(AssessorScoreValidator.class.getName());
            FormValidator requiredFileValidator = formValidatorRepository.findByClazzName(RequiredFileValidator.class.getName());
            FormValidator requiredMultipleChoiceValidator = formValidatorRepository.findByClazzName(RequiredMultipleChoiceValidator.class.getName());

            int si = 0;
            for (Section section : sections) {
                competition.getSections().add(section);
                section.setCompetition(competition);
                section.setParentSection(parent);
                section.setPriority(si);
                si++;
                Section savedSection = sectionRepository.save(section);
                persistAndPrioritiseSections(competition, section.getChildSections(), savedSection);
                peristAndPrioritiesQuestions(competition, section.getQuestions(), savedSection,
                        notEmptyValidator, wordCountValidator, researchCategoryValidator, assessorScopeValidator, assessorScoreValidator, requiredFileValidator,
                        requiredMultipleChoiceValidator);
        }
    }

    @NotSecured("Must be secured by other services.")
    public List<Question> peristAndPrioritiesQuestions(Competition competition, List<Question> questions, Section parent) {
        FormValidator notEmptyValidator = formValidatorRepository.findByClazzName(NotEmptyValidator.class.getName());
        FormValidator wordCountValidator = formValidatorRepository.findByClazzName(WordCountValidator.class.getName());
        FormValidator researchCategoryValidator = formValidatorRepository.findByClazzName(ResearchCategoryValidator.class.getName());
        FormValidator assessorScopeValidator = formValidatorRepository.findByClazzName(AssessorScopeValidator.class.getName());
        FormValidator assessorScoreValidator = formValidatorRepository.findByClazzName(AssessorScoreValidator.class.getName());
        FormValidator requiredFileValidator = formValidatorRepository.findByClazzName(RequiredFileValidator.class.getName());
        FormValidator requiredMultipleChoiceValidator = formValidatorRepository.findByClazzName(RequiredMultipleChoiceValidator.class.getName());
        return peristAndPrioritiesQuestions(competition, questions, parent,
                notEmptyValidator, wordCountValidator, researchCategoryValidator, assessorScopeValidator, assessorScoreValidator, requiredFileValidator,
                requiredMultipleChoiceValidator);
    }

    @NotSecured("Must be secured by other services.")
    public List<Question>  peristAndPrioritiesQuestions(Competition competition, List<Question> questions, Section parent,
                                             FormValidator notEmptyValidator, FormValidator wordCountValidator,
                                             FormValidator researchCategoryValidator, FormValidator assessorScopeValidator,
                                             FormValidator assessorScoreValidator, FormValidator requiredFileValidator,
                                             FormValidator requiredMultipleChoiceValidator) {
        List<Question> savedQuestions = new ArrayList<>();
        int qi = 0;
        for (Question question : questions) {
            question.setSection(parent);
            question.setCompetition(competition);
            question.setPriority(qi);
            qi++;
            if (parent.getName().equals("Application questions")) {
                question.setQuestionNumber(String.valueOf(qi));
            }
            Question savedQuestion = questionRepository.save(question);
            savedQuestions.add(savedQuestion);
            int fii = 0;
            for (FormInput fi : question.getFormInputs()) {
                fi.setQuestion(savedQuestion);
                fi.setCompetition(competition);
                fi.setPriority(fii);
                fii++;
                fi.getMultipleChoiceOptions().forEach(mc -> {
                    mc.setFormInput(fi);
                });
                int gri = 0;
                for (GuidanceRow gr : fi.getGuidanceRows()) {
                    gr.setFormInput(fi);
                    gr.setPriority(gri);
                    gri++;
                }
                switch (fi.getType()) {
                    case TEXTAREA:
                        fi.addFormValidator(notEmptyValidator);
                        fi.addFormValidator(wordCountValidator);
                        break;
                    case FILEUPLOAD:
                        break;
                    case ASSESSOR_RESEARCH_CATEGORY:
                        fi.addFormValidator(notEmptyValidator);
                        fi.addFormValidator(researchCategoryValidator);
                        break;
                    case ASSESSOR_APPLICATION_IN_SCOPE:
                        fi.addFormValidator(notEmptyValidator);
                        fi.addFormValidator(assessorScopeValidator);
                        break;
                    case ASSESSOR_SCORE:
                        fi.addFormValidator(notEmptyValidator);
                        fi.addFormValidator(assessorScoreValidator);
                        break;
                    case TEMPLATE_DOCUMENT:
                        fi.addFormValidator(requiredFileValidator);
                        break;
                    case MULTIPLE_CHOICE:
                        fi.addFormValidator(requiredMultipleChoiceValidator);
                        break;
                }
                formInputRepository.save(fi);
            }
        }
        return savedQuestions;
    }
}
