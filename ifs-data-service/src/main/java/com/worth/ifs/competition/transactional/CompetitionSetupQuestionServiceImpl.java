package com.worth.ifs.competition.transactional;

import com.google.common.collect.Lists;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.assessment.resource.AssessorFormInputType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.ApplicantFormInputType;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.mapper.GuidanceRowMapper;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputTypeRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for operations around the usage and processing of Competitions questions in setup.
 */
@Service
public class CompetitionSetupQuestionServiceImpl extends BaseTransactionalService implements CompetitionSetupQuestionService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputTypeRepository formInputTypeRepository;

    @Autowired
    private GuidanceRowMapper guidanceRowMapper;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        CompetitionSetupQuestionResource setupResource = new CompetitionSetupQuestionResource();

        question.getFormInputs().forEach(formInput -> {
            if(FormInputScope.ASSESSMENT.equals(formInput.getScope())) {
                mapAssessmentFormInput(formInput, setupResource);
            } else {
                mapApplicationFormInput(formInput, setupResource);
            }
        });

        setupResource.setScoreTotal(question.getAssessorMaximumScore());
        setupResource.setNumber(question.getQuestionNumber());
        setupResource.setShortTitle(question.getShortName());
        setupResource.setTitle(question.getName());
        setupResource.setSubTitle(question.getDescription());
        setupResource.setQuestionId(question.getId());
        setupResource.setType(CompetitionSetupQuestionType.typeFromQuestionTitle(question.getShortName()));

        return ServiceResult.serviceSuccess(setupResource);
    }

    private void mapApplicationFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        if (ApplicantFormInputType.FILE_UPLOAD.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setAppendix(formInput.getActive());
        } else if (ApplicantFormInputType.QUESTION.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setGuidanceTitle(formInput.getGuidanceQuestion());
            setupResource.setGuidance(formInput.getGuidanceAnswer());
            setupResource.setMaxWords(wordCountWithDefault(formInput.getWordCount()));
        }
    }

    private void mapAssessmentFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        if (AssessorFormInputType.FEEDBACK.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setWrittenFeedback(formInput.getActive());
            setupResource.setAssessmentMaxWords(wordCountWithDefault(formInput.getWordCount()));
            setupResource.setAssessmentGuidance(formInput.getGuidanceQuestion());
            setupResource.setGuidanceRows(Lists.newArrayList(guidanceRowMapper.mapToResource(formInput.getGuidanceRows())));
        } else if (AssessorFormInputType.SCORE.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setScored(formInput.getActive());
        } else if (AssessorFormInputType.APPLICATION_IN_SCOPE.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setScope(formInput.getActive());
        } else if (AssessorFormInputType.RESEARCH_CATEGORY.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setResearchCategoryQuestion(formInput.getActive());
        }
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        Long questionId = competitionSetupQuestionResource.getQuestionId();
        Question question = questionRepository.findOne(questionId);

        question.setShortName(competitionSetupQuestionResource.getShortTitle());
        question.setName(competitionSetupQuestionResource.getTitle());
        question.setDescription(competitionSetupQuestionResource.getSubTitle());
        question.setAssessorMaximumScore(competitionSetupQuestionResource.getScoreTotal());

        FormInput questionFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputTypeTitle(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.QUESTION.getTitle());
        questionFormInput.setGuidanceQuestion(competitionSetupQuestionResource.getGuidanceTitle());
        questionFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
        questionFormInput.setWordCount(competitionSetupQuestionResource.getMaxWords());

        markAppendixAsActiveOrInactive(questionId, competitionSetupQuestionResource, question, questionFormInput);
        createOrDeleteAppendixFormInput(questionId, competitionSetupQuestionResource, question, questionFormInput);
        createOrDeleteScoredFormInput(questionId, competitionSetupQuestionResource, question, questionFormInput);
        createOrDeleteWrittenFeedbackFormInput(questionId, competitionSetupQuestionResource, question, questionFormInput);

        //TODO INFUND-5685 and INFUND-5631 Save assessor form inputs for AssessorFormInputTypes

        return ServiceResult.serviceSuccess(competitionSetupQuestionResource);
    }

    private void markAppendixAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource, Question question, FormInput questionFormInput) {
        FormInput appendixFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputTypeTitle(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.FILE_UPLOAD.getTitle());
        if (appendixFormInput != null && competitionSetupQuestionResource.getAppendix() != null) {
            appendixFormInput.setActive(competitionSetupQuestionResource.getAppendix());
        }
    }

    private void createOrDeleteScoredFormInput(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource, Question question, FormInput questionFormInput) {

        FormInput scoredFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputTypeTitle(questionId, FormInputScope.ASSESSMENT, AssessorFormInputType.SCORE.getTitle());

        if (competitionSetupQuestionResource.getScored()) {
            if (scoredFormInput == null) {
                scoredFormInput = new FormInput();
            }
            scoredFormInput.setScope(FormInputScope.ASSESSMENT);
            scoredFormInput.setFormInputType(formInputTypeRepository.findOneByTitle(AssessorFormInputType.SCORE.getTitle()));
            scoredFormInput.setQuestion(question);
            scoredFormInput.setCompetition(question.getCompetition());

            if (questionFormInput != null) {
                scoredFormInput.setPriority(questionFormInput.getPriority() + 1);
            } else {
                scoredFormInput.setPriority(0);
            }
            formInputRepository.save(scoredFormInput);

        } else if (scoredFormInput != null) {
            formInputRepository.delete(scoredFormInput);
        }
    }

    private void createOrDeleteWrittenFeedbackFormInput(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource, Question question, FormInput questionFormInput) {

        FormInput writtenFeedbackFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputTypeTitle(questionId, FormInputScope.ASSESSMENT, AssessorFormInputType.FEEDBACK.getTitle());

        if (competitionSetupQuestionResource.getWrittenFeedback()) {
            if (writtenFeedbackFormInput == null) {

                writtenFeedbackFormInput = new FormInput();
            }
            writtenFeedbackFormInput.setScope(FormInputScope.ASSESSMENT);
            writtenFeedbackFormInput.setFormInputType(formInputTypeRepository.findOneByTitle(AssessorFormInputType.FEEDBACK.getTitle()));
            writtenFeedbackFormInput.setQuestion(question);
            writtenFeedbackFormInput.setGuidanceQuestion(competitionSetupQuestionResource.getAssessmentGuidance());
            writtenFeedbackFormInput.setWordCount(competitionSetupQuestionResource.getAssessmentMaxWords());
            writtenFeedbackFormInput.setGuidanceRows(Lists.newArrayList(guidanceRowMapper.mapToDomain(competitionSetupQuestionResource.getGuidanceRows())));
            writtenFeedbackFormInput.setCompetition(question.getCompetition());
            if (questionFormInput != null) {
                writtenFeedbackFormInput.setPriority(questionFormInput.getPriority() + 1);
            } else {
                writtenFeedbackFormInput.setPriority(0);
            }
            formInputRepository.save(writtenFeedbackFormInput);
        } else if (writtenFeedbackFormInput != null) {
            formInputRepository.delete(writtenFeedbackFormInput);
        }
    }

    private int wordCountWithDefault(Integer wordCount) {
        if (wordCount != null && wordCount > 0) {
            return wordCount;
        } else {
            return 400;
        }
    }
}
