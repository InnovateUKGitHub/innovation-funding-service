package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class QuestionServiceImpl extends BaseTransactionalService implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public ServiceResult<QuestionResource> getQuestionById(final Long id) {
        return getQuestionResource(id);
    }

    @Override
    public ServiceResult<List<QuestionResource>> findByCompetition(final Long competitionId) {
        return serviceSuccess(questionsToResources(questionRepository.findByCompetitionId(competitionId)));
    }

    // TODO DW - INFUND-1555 - in situation where next / prev question not found, should this be a 404?
    @Override
    public ServiceResult<QuestionResource> getNextQuestion(final Long questionId) {

        return find(getQuestionSupplier(questionId)).andOnSuccess(question -> {
            Question nextQuestion = null;
            if (question != null) {
                nextQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                        question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

                if (nextQuestion == null) {
                    nextQuestion = getNextQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
                }
            }
            if (nextQuestion == null) {
                return serviceFailure(notFoundError(QuestionResource.class, "getNextQuestion", questionId));
            } else {
                return serviceSuccess(questionMapper.mapToResource(nextQuestion));
            }
        });
    }

    @Override
    public ServiceResult<QuestionResource> getPreviousQuestionBySection(final Long sectionId) {
        return sectionService.getById(sectionId).andOnSuccess(section -> {

            if (section.getParentSection() != null) {
                Optional<SectionResource> previousSection = sectionService.getPreviousSection(section).getOptionalSuccessObject();
                if (previousSection != null) {
                    Optional<Question> lastQuestionInSection = previousSection.get().getQuestions()
                            .stream()
                            .map(questionRepository::findOne)
                            .max(comparing(Question::getPriority));
                    if(lastQuestionInSection.isPresent()){
                        return serviceSuccess(questionMapper.mapToResource(lastQuestionInSection.get()));
                    }
                }
            }
            return serviceFailure(notFoundError(QuestionResource.class, "getPreviousQuestionBySection", sectionId));
        });
    }

    @Override
    public ServiceResult<QuestionResource> getNextQuestionBySection(final Long sectionId) {

        return sectionService.getById(sectionId)
                .andOnSuccess(section -> {

                    if (section.getParentSection() != null) {
                        Optional<SectionResource> nextSection = sectionService.getNextSection(section).getOptionalSuccessObject();
                        if (nextSection.isPresent()) {
                            Optional<Question> firstQuestionInSection = nextSection.get().getQuestions()
                                    .stream()
                                    .map(questionRepository::findOne)
                                    .min(comparing(Question::getPriority));

                            if (firstQuestionInSection.isPresent()) {
                                return serviceSuccess(questionMapper.mapToResource(firstQuestionInSection.get()));
                            }
                        }
                    }
                    return serviceFailure(notFoundError(QuestionResource.class, "getNextQuestionBySection", sectionId));
                });
    }

    @Override
    public ServiceResult<QuestionResource> getPreviousQuestion(final Long questionId) {
        return find(getQuestionSupplier(questionId)).andOnSuccess(question -> {
            Question previousQuestion = null;
            if (question != null) {
                previousQuestion = questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                        question.getCompetition().getId(), question.getSection().getId(), question.getPriority());

                if (previousQuestion == null) {
                    previousQuestion = getPreviousQuestionBySection(question.getSection().getId(), question.getCompetition().getId());
                }
            }
            if (previousQuestion == null) {
                return serviceFailure(notFoundError(QuestionResource.class, "getPreviousQuestion", questionId));
            } else {
                return serviceSuccess(questionMapper.mapToResource(previousQuestion));
            }
        });
    }

    @Override
    public ServiceResult<Question> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType) {
        List<Question> questions = questionRepository.findByCompetitionId(competitionId);
        Optional<Question> question = simpleFindFirst(questions, q -> {
            List<FormInput> activeFormInputs = simpleFilter(q.getFormInputs(), FormInput::getActive);
            return !activeFormInputs.isEmpty() && formInputType == activeFormInputs.get(0).getType();
        });
        if (question.isPresent()) {
            return serviceSuccess(question.get());
        } else {
            return serviceFailure(notFoundError(Question.class, competitionId, formInputType));
        }
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionByCompetitionIdAndCompetitionSetupQuestionType(final long competitionId,
                                                                                                     final CompetitionSetupQuestionType competitionSetupQuestionType) {
        return find(questionRepository.findFirstByCompetitionIdAndQuestionSetupType(competitionId,
                competitionSetupQuestionType), notFoundError(Question.class, competitionId, competitionSetupQuestionType)).andOnSuccessReturn(questionMapper::mapToResource);
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionResourceByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInpuType) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInpuType).andOnSuccessReturn(questionMapper::mapToResource);
    }

    @Override
	public ServiceResult<List<QuestionResource>> getQuestionsBySectionIdAndType(
			Long sectionId, QuestionType type) {
		return getSection(sectionId).andOnSuccessReturn(section -> questionsOfType(section, type));
	}

    @Override
    @Transactional
    public ServiceResult<QuestionResource> save(QuestionResource questionResource) {
        Question questionUpdated = questionRepository.save(questionMapper.mapToDomain(questionResource));
        return serviceSuccess(questionMapper.mapToResource(questionUpdated));
    }

    @Override
    public ServiceResult<List<QuestionResource>> getQuestionsByAssessmentId(Long assessmentId) {
        return find(getAssessment(assessmentId)).andOnSuccess(assessment ->
             sectionService.getByCompetitionIdVisibleForAssessment(
                    applicationRepository.findOne(assessment.getParticipant().getApplicationId()).getCompetition().getId())
                    .andOnSuccessReturn(sections -> sections.stream().map(sectionMapper::mapToDomain)
                            .flatMap(section -> section.getQuestions().stream()).map(questionMapper::mapToResource)
                            .collect(toList())));
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionByIdAndAssessmentId(Long questionId, Long assessmentId) {
        return find(getAssessment(assessmentId), getQuestionSupplier(questionId)).andOnSuccess((assessment, question) -> {
            if (question.getCompetition().getId().equals(assessment.getTarget().getCompetition().getId())) {
                return serviceSuccess(questionMapper.mapToResource(question));
            }
            return serviceFailure(notFoundError(Question.class, questionId, assessmentId));
        });
    }

    private List<QuestionResource> questionsOfType(Section section, QuestionType type) {
    	Stream<Question> sectionQuestionsStream = section.getQuestions().stream();
    	Stream<Question> childSectionsQuestionsStream = section.getChildSections().stream().flatMap(s -> s.getQuestions().stream());
    	
    	return Stream.concat(sectionQuestionsStream, childSectionsQuestionsStream).filter(q -> q.isType(type))
        .map(questionMapper::mapToResource)
        .collect(toList());
    }
    


    private Question getNextQuestionBySection(Long section, Long competitionId) {
        SectionResource nextSection = sectionService.getNextSection(section).getSuccessObjectOrNull();
        if (nextSection != null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(competitionId, nextSection.getId());
        }
        return null;
    }

    private Question getPreviousQuestionBySection(Long section, Long competitionId) {
        SectionResource previousSection = sectionService.getPreviousSection(section).getSuccessObjectOrNull();

        if (previousSection != null) {
            return questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(competitionId, previousSection.getId());
        }
        return null;
    }

    private Supplier<ServiceResult<Assessment>> getAssessment(Long assessmentId) {
        return () -> find(assessmentRepository.findOne(assessmentId), notFoundError(Assessment.class, assessmentId));
    }

    private Supplier<ServiceResult<Question>> getQuestionSupplier(Long questionId) {
        return () -> find(questionRepository.findOne(questionId), notFoundError(Question.class, questionId));
    }

    private ServiceResult<QuestionResource> getQuestionResource(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(QuestionResource.class, questionId)).andOnSuccessReturn(questionMapper::mapToResource);
    }

    private List<QuestionResource> questionsToResources(List<Question> filtered) {
        return simpleMap(filtered, question -> questionMapper.mapToResource(question));
    }
}
