package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.assessment.resource.AssessmentEvent.FEEDBACK;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Transactional service providing functions for creating full or partial copies of competition templates.
 */
@Service
public class CompetitionSetupTemplateServiceImpl extends BaseTransactionalService implements CompetitionSetupTemplateService {
    @Autowired
    private GuidanceRowRepository guidanceRowRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private DefaultApplicationQuestionFactory defaultApplicationQuestionFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ServiceResult<Question> addDefaultQuestionToCompetition(Competition competition) {
        Section applicationQuestionsSection = sectionRepository.findByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildDefaultQuestion(competition);
        return serviceSuccess(createQuestionFunction(competition, applicationQuestionsSection).apply(question));
    }

    @Override
    @Transactional
    public ServiceResult<Competition> createCompetitionByCompetitionTemplate(Competition competition, Competition template) {
        cleanUpCompetitionSections(competition);

        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        List<Section> sectionsWithoutParentSections = template.getSections().stream()
                .filter(s -> s.getParentSection() == null)
                .collect(Collectors.toList());


        attachSections(competition, sectionsWithoutParentSections, null);

        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());

        return serviceSuccess(competitionRepository.save(competition));
    }

    private void attachSections(Competition competition, List<Section> sectionTemplates, Section parentSection) {
        if (sectionTemplates == null) {
            return;
        }
        new ArrayList<>(sectionTemplates).forEach(attachSection(competition, parentSection));
    }

    private Consumer<Section> attachSection(Competition competition, Section parentSection) {
        return (Section section) -> {
            entityManager.detach(section);
            section.setCompetition(competition);
            section.setId(null);
            sectionRepository.save(section);
            if (!competition.getSections().contains(section)) {
                competition.getSections().add(section);
            }

            section.setQuestions(createQuestions(competition, section, section.getQuestions()));

            attachSections(competition, section.getChildSections(), section);

            section.setParentSection(parentSection);
            if (parentSection != null) {
                if (parentSection.getChildSections() == null) {
                    parentSection.setChildSections(new ArrayList<>());
                }
                if (!parentSection.getChildSections().contains(section)) {
                    parentSection.getChildSections().add(section);
                }
            }
        };
    }

    private void cleanUpCompetitionSections(Competition competition) {
        List<GuidanceRow> scoreRows = guidanceRowRepository.findByFormInputQuestionCompetitionId(competition.getId());
        guidanceRowRepository.delete(scoreRows);

        List<FormInput> formInputs = formInputRepository.findByCompetitionId(competition.getId());
        formInputRepository.delete(formInputs);

        List<Question> questions = questionRepository.findByCompetitionId(competition.getId());
        questionRepository.delete(questions);

        List<Section> sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competition.getId());
        competition.setSections(new ArrayList());
        sectionRepository.delete(sections);
    }

    private Function<Question, Question> createQuestionFunction(Competition competition, Section section) {
        return (Question question) -> {
            entityManager.detach(question);
            question.setCompetition(competition);
            question.setSection(section);
            question.setId(null);
            questionRepository.save(question);

            question.setFormInputs(createFormInputs(competition, question, question.getFormInputs()));
            return question;
        };
    }

    public List<Question> createQuestions(Competition competition, Section section, List<Question> questions) {
        return simpleMap(questions, createQuestionFunction(competition, section));
    }

    private List<FormInput> createFormInputs(Competition competition, Question question, List<FormInput> formInputTemplates) {
        return simpleMap(formInputTemplates, createFormInput(competition, question));
    }

    private Function<FormInput, FormInput> createFormInput(Competition competition, Question question) {
        return (FormInput formInput) -> {
            // Extract the validators into a new Set as the hibernate Set contains persistence information which alters
            // the original FormValidator
            Set<FormValidator> copy = new HashSet<>(formInput.getFormValidators());
            entityManager.detach(formInput);
            formInput.setCompetition(competition);
            formInput.setQuestion(question);
            formInput.setId(null);
            formInput.setFormValidators(copy);
            formInput.setActive(isSectorCompetitionWithScopeQuestion(competition, question, formInput) ? false : formInput.getActive());
            formInputRepository.save(formInput);
            formInput.setGuidanceRows(createFormInputGuidanceRows(formInput, formInput.getGuidanceRows()));
            return formInput;
        };
    }

    private boolean isSectorCompetitionWithScopeQuestion(Competition competition, Question question, FormInput formInput) {
        if (competition.getCompetitionType().isSector() && question.isScope()) {
            if (formInput.getType() == ASSESSOR_APPLICATION_IN_SCOPE || formInput.getDescription().equals(FEEDBACK)) {
                return true;
            }
        }
        return false;
    }

    private List<GuidanceRow> createFormInputGuidanceRows(FormInput formInput, List<GuidanceRow> guidanceRows) {
        return simpleMap(guidanceRows, createFormInputGuidanceRow(formInput));
    }

    private Function<GuidanceRow, GuidanceRow> createFormInputGuidanceRow(FormInput formInput) {
        return (GuidanceRow row) -> {
            entityManager.detach(row);
            row.setFormInput(formInput);
            row.setId(null);
            guidanceRowRepository.save(row);
            return row;
        };
    }
}
