package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
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

    @Autowired
    private FormInputRepository formInputRepository;

    @Override
    public ServiceResult<QuestionResource> getQuestionById(final Long id) {
        return getQuestionResource(id);
    }

    @Override
    @Cacheable(cacheNames="questionsByCompetition",
            key = "T(java.lang.String).format('questionsByCompetition:%d', #competitionId)",
            unless = "!T(org.innovateuk.ifs.cache.CacheHelper).cacheResultIfCompetitionIsOpen(#result)")
    public ServiceResult<List<QuestionResource>> findByCompetition(final Long competitionId) {
        return serviceSuccess(questionsToResources(questionRepository.findByCompetitionId(competitionId)));
    }

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
                if (previousSection.isPresent()) {
                    Optional<Question> lastQuestionInSection = previousSection.get().getQuestions()
                            .stream()
                            .map(questionRepository::findById)
                            .map(Optional::get)
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
                                    .map(questionRepository::findById)
                                    .map(Optional::get)
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

        List<FormInput> formInputs = formInputRepository.findByCompetitionIdAndTypeIn(competitionId, singletonList(formInputType));

        if (!formInputs.isEmpty()) {
            return serviceSuccess(formInputs.get(0).getQuestion());
        } else {
            return serviceFailure(notFoundError(Question.class, competitionId, formInputType));
        }
    }

    @Override
    public ServiceResult<QuestionResource> getQuestionByCompetitionIdAndQuestionSetupType(final long competitionId,
                                                                                          final QuestionSetupType questionSetupType) {
        return find(questionRepository.findFirstByCompetitionIdAndQuestionSetupType(competitionId,
                questionSetupType), notFoundError(Question.class, competitionId, questionSetupType)).andOnSuccessReturn(questionMapper::mapToResource);
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
                    applicationRepository.findById(assessment.getParticipant().getApplicationId()).get().getCompetition().getId())
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
        return () -> find(assessmentRepository.findById(assessmentId), notFoundError(Assessment.class, assessmentId));
    }

    private Supplier<ServiceResult<Question>> getQuestionSupplier(Long questionId) {
        return () -> find(questionRepository.findById(questionId), notFoundError(Question.class, questionId));
    }

    private ServiceResult<QuestionResource> getQuestionResource(Long questionId) {
        return find(questionRepository.findById(questionId), notFoundError(QuestionResource.class, questionId)).andOnSuccessReturn(questionMapper::mapToResource);
    }

    private List<QuestionResource> questionsToResources(List<Question> filtered) {
        return simpleMap(filtered, question -> questionMapper.mapToResource(question));
    }
}
