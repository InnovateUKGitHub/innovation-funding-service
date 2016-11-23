package com.worth.ifs.competition.transactional;

import com.google.common.collect.Lists;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.repository.*;
import com.worth.ifs.assessment.resource.*;
import com.worth.ifs.commons.service.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.form.domain.*;
import com.worth.ifs.form.mapper.*;
import com.worth.ifs.form.repository.*;
import com.worth.ifs.form.resource.*;
import com.worth.ifs.transactional.*;
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

    public static String SCOPE_IDENTIFIER = "Scope";
    private static String PROJECT_SUMMARY_IDENTIFIER = "Project summary";
    private static String PUBLIC_DESCRIPTION_IDENTIFIER = "Public description";

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
        setupResource.setType(typeFromQuestion(question));

        return ServiceResult.serviceSuccess(setupResource);
    }

    private CompetitionSetupQuestionType typeFromQuestion(Question question) {
        if (question.getShortName().equals(SCOPE_IDENTIFIER)) {
            return CompetitionSetupQuestionType.SCOPE;
        } else if (question.getShortName().equals(PROJECT_SUMMARY_IDENTIFIER)) {
            return CompetitionSetupQuestionType.PROJECT_SUMMARY;
        } else if (question.getShortName().equals(PUBLIC_DESCRIPTION_IDENTIFIER)) {
            return CompetitionSetupQuestionType.PUBLIC_DESCRIPTION;
        } else {
            return CompetitionSetupQuestionType.ASSESSED_QUESTION;
        }
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
            setupResource.setScope(true);
        } else if (AssessorFormInputType.RESEARCH_CATEGORY.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setResearchCategoryQuestion(true);
        }
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        Long questionId = competitionSetupQuestionResource.getQuestionId();
        Question question = questionRepository.findOne(questionId);

        question.setShortName(competitionSetupQuestionResource.getShortTitle());
        question.setName(competitionSetupQuestionResource.getTitle());
        question.setDescription(competitionSetupQuestionResource.getSubTitle());

        FormInput questionFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputTypeTitle(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.QUESTION.getTitle());
        questionFormInput.setGuidanceQuestion(competitionSetupQuestionResource.getGuidanceTitle());
        questionFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
        questionFormInput.setWordCount(competitionSetupQuestionResource.getMaxWords());

        markAppendixAsActiveOrInactive(questionId, competitionSetupQuestionResource, question, questionFormInput);

        //TODO INFUND-5685 and INFUND-5631 Save assessor form inputs for AssessorFormInputTypes

        return ServiceResult.serviceSuccess(competitionSetupQuestionResource);
    }

    private void markAppendixAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource, Question question, FormInput questionFormInput) {
        FormInput appendixFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputTypeTitle(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.FILE_UPLOAD.getTitle());
        if (appendixFormInput != null && competitionSetupQuestionResource.getAppendix() != null) {
            appendixFormInput.setActive(competitionSetupQuestionResource.getAppendix());
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
